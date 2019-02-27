package cn.itcast.bos.service.take_delivery.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.AreaRepository;
import cn.itcast.bos.dao.base.FixedAreaRepository;
import cn.itcast.bos.dao.take_delivery.OrderRepository;
import cn.itcast.bos.dao.take_delivery.WorkBillRepository;
import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.domain.constant.Constants;
import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.bos.domain.take_delivery.WorkBill;
import cn.itcast.bos.service.take_delivery.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private FixedAreaRepository fixedAreaRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	private WorkBillRepository workBillRepository;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Override
	public void saveOrder(Order order) {
		// 注入订单基本信息
		order.setOrderNum(UUID.randomUUID().toString());// 订单号
		order.setStatus("1");// 待取件
		order.setOrderTime(new Date());// 下单时间
		// 寄件人地区
		Area sendArea = order.getSendArea();
		Area persistArea = areaRepository.findByProvinceAndCityAndDistrict(sendArea.getProvince(), sendArea.getCity(),
				sendArea.getDistrict());
		order.setSendArea(persistArea);
		// 收件人地区
		Area recArea = order.getRecArea();
		Area recPersistArea = areaRepository.findByProvinceAndCityAndDistrict(recArea.getProvince(), recArea.getCity(),
				recArea.getDistrict());
		order.setRecArea(recPersistArea);

		// 将订单分单给快递员,然后保存
		// 1.基于crm地址完全匹配自动分单:传递sendAddress到crm系统得到匹配的客户的定区,从而得到定区匹配的快递员进行分单
		String fixedAreaId = WebClient.create(
				Constants.CRM_MANAGEMENT_SERVICE + "/findFixedAreaIdByAddress?address=" + order.getSendAddress())
				.accept(MediaType.APPLICATION_JSON).get(String.class);
		if (fixedAreaId != null) {
			// 找到匹配的定区后,获得定区对象,得到对应的快递员集合
			FixedArea fixedArea = fixedAreaRepository.findOne(fixedAreaId);
			Iterator<Courier> iterator = fixedArea.getCouriers().iterator();
			if (iterator.hasNext()) {
				// 得到快递员
				Courier courier = iterator.next();
				if (courier != null) {
					// 关联快递员后保存订单
					savaOrderWithCourier(order, courier);
					System.out.println("自动分单成功...");

					// 生成工单,发送短信
					generateWorkBill(order);
					return;
				}
			}
		}

		// 2.crm地址中没有匹配的客户时,通过省市区得到Area区域对象,得到定区集合,遍历集合通过定区中的关键字,如果地址中包含关键字或者辅助关键字,则按照对应的定区匹配分区,分配快递员
		// 遍历关联的定区集合
		Set<SubArea> subareas = persistArea.getSubareas();
		for (SubArea subArea : subareas) {
			// 判断寄件地址包含定区的关键字或者辅助关键字
			if (order.getSendAddress().contains(subArea.getKeyWords())
					|| order.getSendAddress().contains(subArea.getAssistKeyWords())) {
				// 找到定区,得到对应的分区,得到对应的快递员
				Iterator<Courier> iterator = subArea.getFixedArea().getCouriers().iterator();
				if (iterator.hasNext()) {
					// 得到快递员
					Courier courier = iterator.next();
					if (courier != null) {
						savaOrderWithCourier(order, courier);
						System.out.println("自动分单成功...");

						// 生成工单,发送短信
						generateWorkBill(order);
						return;
					}
				}
			}
		}

		// 3.都没有匹配的分区,则进行人工分单
		order.setOrderType("2");
		orderRepository.save(order);
	}

	// 将订单关联快递员,保存订单
	private void savaOrderWithCourier(Order order, Courier courier) {
		order.setCourier(courier);
		order.setOrderType("1");
		orderRepository.save(order);
	}

	// 生成工单,发送短信
	private void generateWorkBill(final Order order) {
		//生成工单
		WorkBill workBill = new WorkBill();
		workBill.setType("新");
		workBill.setPickstate("新单");
		workBill.setBuildtime(new Date());
		workBill.setRemark(order.getRemark());
		final String smsNumber = RandomStringUtils.randomNumeric(4);
		workBill.setSmsNumber(smsNumber); // 短信序号
		workBill.setOrder(order);
		workBill.setCourier(order.getCourier());
		workBillRepository.save(workBill);
		
		//调用MQ服务,发送短信
		jmsTemplate.send("bos_sms", new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("telephone", order.getCourier().getTelephone());
				mapMessage.setString("content", "短信序号：" + smsNumber + ",取件地址：" + order.getSendAddress()
				+ ",联系人:" + order.getSendName() + ",手机:"
				+ order.getSendMobile() + "，快递员捎话："
				+ order.getSendMobileMsg());
				
				return mapMessage;
			}
		});
		
		//修改工单状态
		workBill.setPickstate("已通知");
	}

	@Override
	public Order findByOrderNum(String orderNum) {
		return orderRepository.findByOrderNum(orderNum);
	}

}
