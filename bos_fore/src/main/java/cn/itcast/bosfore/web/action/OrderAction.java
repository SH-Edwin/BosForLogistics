package cn.itcast.bosfore.web.action;

import javax.servlet.Servlet;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.constant.Constants;
import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.crm.domain.Customer;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class OrderAction extends ActionSupport implements ModelDriven<Order>{
	
	private Order order = new Order();
	
	@Override
	public Order getModel() {
		return order;
	}
	
	//属性驱动获得省市区
	private String sendAreaInfo;
	private String recAreaInfo;
	
	public void setSendAreaInfo(String sendAreaInfo) {
		this.sendAreaInfo = sendAreaInfo;
	}


	public void setRecAreaInfo(String recAreaInfo) {
		this.recAreaInfo = recAreaInfo;
	}


	//下单
	@Action(value="order_add", results= {@Result(name="success", type="redirect",location="index.html")})
	public String add() {
		//关联登陆用户,封装customer_id
		Customer customer=(Customer) ServletActionContext.getRequest().getSession().getAttribute("customer");
		order.setCustomer_id(customer.getId());
		//封装Area
		String[] sendAreaData = sendAreaInfo.split("/");
		Area sendArea=new Area();
		sendArea.setProvince(sendAreaData[0]);
		sendArea.setCity(sendAreaData[1]);
		sendArea.setDistrict(sendAreaData[2]);
		order.setSendArea(sendArea);
		
		String[] recAreaData = recAreaInfo.split("/");
		Area recArea=new Area();
		recArea.setProvince(recAreaData[0]);
		recArea.setCity(recAreaData[1]);
		recArea.setDistrict(recAreaData[2]);
		order.setRecArea(recArea);
		
		//基于webservice向后端传递数据
		WebClient.create(Constants.BOS_MANAGEMENT_SERVICE+"/orderService/add").
		type(MediaType.APPLICATION_JSON).post(order);
		return SUCCESS;
	}
	
}
