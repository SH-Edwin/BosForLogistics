package cn.itcast.bos.web.action.take_delivery;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.hibernate.cfg.beanvalidation.ActivationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.parser.Part.IgnoreCaseType;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.service.take_delivery.WayBillService;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class WayBillAction extends ActionSupport implements ModelDriven<WayBill>{
	
	private WayBill wayBill=new WayBill();
	@Override
	public WayBill getModel() {
		return wayBill;
	}
	
	@Autowired
	private WayBillService wayBillService;
	
	//保存运单
	@Action(value="waybill_save",results={@Result(name="success",type="json")})
	public String save() {
		//去除没有id的order对象
		if (wayBill.getOrder()!=null && (wayBill.getOrder().getId()==null||wayBill.getOrder().getId()==0)) {
			wayBill.setOrder(null);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			wayBillService.save(wayBill);
			map.put("success", true);
			map.put("msg", "保存运单成功!");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "保存运单失败...");
		}
		ActionContext.getContext().getValueStack().push(map);
		return SUCCESS;
		
	}

	//无条件查询
	private Integer page;
	private Integer rows;
	public void setPage(Integer page) {
		this.page = page;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	@Action(value="waybill_pageQuery" , results= {@Result(name="success", type="json")})
	public String pageQuery() {
		Pageable pageable=new PageRequest(page-1, rows, new Sort(new Sort.Order(Sort.Direction.DESC, "id")));
		Page<WayBill> page = wayBillService.getPageData(wayBill,pageable);
		Map<String, Object> map = new HashMap<String , Object>();
		map.put("total", page.getTotalElements());
		map.put("rows", page.getContent());
		ActionContext.getContext().getValueStack().push(map);
		return SUCCESS;
	}
	
	//反写内容
	@Action(value="wayBill_findByWayBillNum" ,results= {@Result(name="success", type="json")})
	public String findByWayBillNum() {
		Map<String, Object> map=new HashMap<String, Object>();
		WayBill resultWayBill=wayBillService.findByWayBillNum(wayBill.getWayBillNum());
		if (resultWayBill != null) {
			map.put("success", true);
			map.put("wayBillData", resultWayBill);
		} else {
			map.put("success", false);
		}
		ActionContext.getContext().getValueStack().push(map);
		return SUCCESS;
	}
	
}
