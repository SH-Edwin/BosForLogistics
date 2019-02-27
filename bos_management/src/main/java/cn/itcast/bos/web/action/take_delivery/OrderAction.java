package cn.itcast.bos.web.action.take_delivery;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.bos.service.take_delivery.OrderService;

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
	
	@Autowired
	private OrderService orderService;
	
	//反写订单内容
	@Action(value="order_findByOrderNum" , results= {@Result(name="success",type="json")})
	public String findByOrderNum() {
		Map<String, Object> map=new HashMap<String, Object>();
		Order resultOrder = orderService.findByOrderNum(order.getOrderNum());
		if (resultOrder != null) {
			map.put("success", true);
			map.put("orderData", resultOrder);
		}else {
			map.put("success", false);
		}
		ActionContext.getContext().getValueStack().push(map);
		return SUCCESS;
	}
}
