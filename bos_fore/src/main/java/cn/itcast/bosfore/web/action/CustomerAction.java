package cn.itcast.bosfore.web.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bosfore.utils.MailUtil;
import cn.itcast.bosfore.utils.SmsUtils;
import cn.itcast.crm.domain.Customer;

public class CustomerAction extends ActionSupport implements ModelDriven<Customer> {

	private Customer customer = new Customer();

	@Override
	public Customer getModel() {
		return customer;
	}
	
	@Autowired
	private JmsTemplate jmsTemplate;

	// 发送验证码短信
	@Action(value = "customer_sendSms")
	public String sendSms() throws UnsupportedEncodingException {
		// 手机号保存在customer内
		// 生成验证码
		String checkCode = new RandomStringUtils().randomNumeric(4);
		System.out.println("手机验证码为:" + checkCode);
		// 将验证码保存在session中
		ServletActionContext.getRequest().getSession().setAttribute(customer.getTelephone(), checkCode);
		
		// 编辑短信内容
		final String content="尊敬的用户您好,本次获取的验证码为:"+checkCode+",服务方:itcast";
		
		//作为生产者创建短信消息队列
		jmsTemplate.send("bos_sms", new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("telephone", customer.getTelephone());
				mapMessage.setString("content", content);
				return mapMessage;
			}
		});
		
		return NONE;
	}

	// 用户注册
	private String checkCode;

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Action(value = "customer_regist", results = {
			@Result(name = "success", type = "redirect", location = "signup-success.html"),
			@Result(name = "input", type = "redirect", location = "signup.html") })
	public String registe() {
		// 校验验证码
		// 获取session内的验证码
		String checkCodeInSession = (String) ServletActionContext.getRequest().getSession()
				.getAttribute(customer.getTelephone());
		if (checkCodeInSession != null && checkCode.equals(checkCodeInSession)) {
			// 通过校验,调用crm的webservice存入对象
			WebClient.create("http://localhost:9002/crm_management/services/customerService/regist")
					.type(MediaType.APPLICATION_JSON).post(customer);
			// 发送激活邮件
			// 生成激活码
			String activeCode = RandomStringUtils.randomNumeric(24);
			// 将激活码存入redis,24小时有效期
			redisTemplate.opsForValue().set(customer.getTelephone(), activeCode, 24, TimeUnit.HOURS);
			;
			// 将激活邮件发送给用户
			String content = "尊敬的用户您好: 请点击以下链接激活您的账号:<br/><a href='" + MailUtil.activeUrl + "?telephone="
					+ customer.getTelephone() + "&activeCode=" + activeCode + "'>速通账号激活链接</a>";
			MailUtil.sendMail("速通账号激活邮件", content, customer.getEmail());
			return SUCCESS;
		} else {
			// 校验失败,跳转
			System.out.println("短信验证码校验失败");
			return INPUT;
		}
	}

	// 激活邮件
	// 接收激活码
	private String activeCode;

	public void setActiveCode(String activeCode) {
		this.activeCode = activeCode;
	}

	@Action(value = "customer_activeMail")
	public String activeMail() throws IOException {
		ServletActionContext.getResponse().setContentType("text/html;charset=utf-8");
		// 判断激活码是否有效
		String activeCodeInRedis = redisTemplate.opsForValue().get(customer.getTelephone());
		if (activeCodeInRedis == null || !activeCode.equals(activeCodeInRedis)) {
			// 激活码无效
			ServletActionContext.getResponse().getWriter().println("激活码无效!");
		} else {
			// 如果激活码有效,调用crm的webservice根据telephone找到用户,判断是否已经激活
			Customer customer2 = WebClient.create("http://localhost:9002/crm_management/services/customerService/customer/findByTelephone/"
					+ customer.getTelephone()).accept(MediaType.APPLICATION_JSON).get(Customer.class);
			if (customer2.getType() == null || customer2.getType() != 1) {
				//没有激活,执行激活用户
				WebClient.create("http://localhost:9002/crm_management/services/customerService/customer/active/"
						+ customer.getTelephone()).put(null);
				ServletActionContext.getResponse().getWriter().println("用户激活成功!");
			}else {
				//已经激活
				ServletActionContext.getResponse().getWriter().println("用户已激活,请勿重复操作!");
			}
		}

		return null;
	}

}
