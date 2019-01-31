package cn.itcast.bos.web.action.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.service.base.CourierService;
import cn.itcast.bos.service.base.FixedAreaService;
import cn.itcast.bos.service.base.TakeTimeService;
import cn.itcast.crm.domain.Customer;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class FixedAreaAction extends ActionSupport implements ModelDriven<FixedArea> {

	private FixedArea fixedArea = new FixedArea();

	@Override
	public FixedArea getModel() {
		return fixedArea;
	}

	@Autowired
	private FixedAreaService fixedAreaService;

	// 增加定区
	@Action(value = "fixedArea_add", results = {
			@Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String save() {
		fixedAreaService.save(fixedArea);
		return SUCCESS;
	}

	// 定区查询
	private Integer page;
	private Integer rows;

	public void setPage(Integer page) {
		this.page = page;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	@Action(value = "fixedArea_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {
		// 查询规则
		Specification<FixedArea> specification = new Specification<FixedArea>() {

			@Override
			public Predicate toPredicate(Root<FixedArea> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// 断言集合
				List<Predicate> list = new ArrayList<Predicate>();
				// 判断查询条件
				if (StringUtils.isNotBlank(fixedArea.getId())) {
					Predicate p1 = cb.equal(root.get("id").as(String.class), fixedArea.getId());
					list.add(p1);
				}
				if (StringUtils.isNotBlank(fixedArea.getCompany())) {
					Predicate p2 = cb.like(root.get("company").as(String.class), "%" + fixedArea.getCompany() + "%");
					list.add(p2);
				}
				return cb.and(list.toArray(new Predicate[0]));
			}
		};

		Pageable pageable = new PageRequest(page - 1, rows);
		Page<FixedArea> page = fixedAreaService.findPageData(specification, pageable);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", page.getTotalElements());
		map.put("rows", page.getContent());
		ActionContext.getContext().getValueStack().push(map);
		return SUCCESS;
	}

	// 查询未关联的客户列表
	@Action(value = "fixedArea_findNoAssociationCustomers", results = { @Result(name = "success", type = "json") })
	public String findNoAssociationCustomers() {
		Collection<? extends Customer> collection = WebClient
				.create("http://localhost:9002/crm_management/services/customerService/noassociationcustomers")
				.accept(MediaType.APPLICATION_JSON).getCollection(Customer.class);
		ActionContext.getContext().getValueStack().push(collection);
		return SUCCESS;
	}

	// 查询关联的客户列表
	@Action(value = "fixedArea_findAssociationCustomers", results = { @Result(name = "success", type = "json") })
	public String findAssociationCustomers() {
		Collection<? extends Customer> collection = WebClient
				.create("http://localhost:9002/crm_management/services/customerService/associationcustomers/"
						+ fixedArea.getId())
				.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).getCollection(Customer.class);
		ActionContext.getContext().getValueStack().push(collection);
		return SUCCESS;
	}

	// 关联客户
	private String[] customerIds;

	public void setCustomerIds(String[] customerIds) {
		this.customerIds = customerIds;
	}

	@Action(value = "fixedArea_associationCustomersToFixedArea", results = {
			@Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String associationCustomersToFixedArea() {
		String cids = StringUtils.join(customerIds, ",");
		System.out.println(cids);
		WebClient.create("http://localhost:9002/crm_management/services/customerService/associationcustomerstofixedarea?"
				+ "customersIdStr="+cids+"&fixedAreaId="+fixedArea.getId()).post(null);
		return SUCCESS;
	}
	
	//关联快递员
	private Integer courierId;
	private Integer takeTimeId;
	
	
	public void setCourierId(Integer courierId) {
		this.courierId = courierId;
	}

	public void setTakeTimeId(Integer takeTimeId) {
		this.takeTimeId = takeTimeId;
	}

	@Action(value = "fixedArea_associationCourierToFixedArea", results = {
			@Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String associationCourierToFixedArea() {
		fixedAreaService.associationCourierToFixedArea(fixedArea.getId(),courierId,takeTimeId);
		return SUCCESS;
	}
	
	//查询所有定区
	@Action(value="fixedArea_findAll", results= {@Result(name="success", type="json")})
	public String findAll() {
		List<FixedArea> list = fixedAreaService.findAll();
		ActionContext.getContext().getValueStack().push(list);
		return SUCCESS;
	}
}
