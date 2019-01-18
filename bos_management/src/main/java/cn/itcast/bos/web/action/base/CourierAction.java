package cn.itcast.bos.web.action.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.xmlbeans.impl.common.IdentityConstraint.IdState;
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
import com.sun.xml.internal.ws.util.xml.CDATA;

import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.base.CourierService;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class CourierAction extends ActionSupport implements ModelDriven<Courier>{
	//派送员模型
	private Courier courier=new Courier();
	
	@Override
	public Courier getModel() {
		return courier;
	}
	//页面请求数据
	private Integer page;
	private Integer rows;
	
	public void setPage(Integer page) {
		this.page = page;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	@Autowired
	private CourierService courierService;
	
	//保存修改派送员
	@Action(value="courier_save",results= {@Result(name="success",type="redirect",location="./pages/base/courier.html")})
	public String save() {
		courierService.save(courier);
		return SUCCESS;
	}
	
	//带条件查询派送员页面
	@Action(value="courier_pageQuery", results= {@Result(name="success",type="json") })
	public String findAll() {
		//接收页面请求信息page,rows,存入pageable对象
		Pageable pageRequest=new PageRequest(page-1, rows);
		
		//接收页面的条件:courierNum,standard.name,company,type,创建specification对象,和pageable对象一起传入service
		Specification<Courier> courierSpecification=new Specification<Courier>() {
			@Override
			public Predicate toPredicate(Root<Courier> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				//predicate集合
				List<Predicate> predicates=new ArrayList<Predicate>();
				if (StringUtils.isNotBlank(courier.getCourierNum())) {
					//courierNum=?
					Predicate p1 = cb.equal(root.get("courierNum").as(String.class), courier.getCourierNum());
					predicates.add(p1);
				}
				if (StringUtils.isNotBlank(courier.getCompany())) {
					//company like %?%
					Predicate p2 = cb.like(root.get("company").as(String.class), "%"+courier.getCompany()+"%");
					predicates.add(p2);
				}
				if (StringUtils.isNotBlank(courier.getType())) {
					//type=?
					Predicate p3 = cb.equal(root.get("type").as(String.class), courier.getType());
					predicates.add(p3);
				}
				//多表查询,root应该关联standard
				Join<Courier,Standard> standardJoin = root.join("standard",JoinType.INNER);
				if (courier.getStandard()!=null && StringUtils.isNotBlank(courier.getStandard().getName())) {
					//standard.name like %?%
					Predicate p4 = cb.like(standardJoin.get("name").as(String.class), "%"+courier.getStandard().getName()+"%");
					predicates.add(p4);
				}
				
				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		
		Page<Courier> page=courierService.getPage(pageRequest,courierSpecification);
		//将页面对象封装成map,包含total,rows,转化成json传递给前端
		Map<String, Object> pageMap=new HashMap<String, Object>();
		pageMap.put("total", page.getTotalElements());
		pageMap.put("rows", page.getContent());
		ActionContext.getContext().getValueStack().push(pageMap);
		return SUCCESS;
	}
	
	//接收作废+还原ids
	private String ids;
	
	public void setIds(String ids) {
		this.ids = ids;
	}

	//作废派送员
	@Action(value="courier_delBatch",results= {@Result(name=SUCCESS,type="redirect", location="./pages/base/courier.html")})
	public String delBatch() {
		String[] idStrings = ids.split(",");
		courierService.delBatch(idStrings);
		return SUCCESS;
	}
	
	//还原派送员
	@Action(value="courier_restoreBatch",results= {@Result(name="success",type="redirect", location="./pages/base/courier.html")})
	public String restoreBatch() {
		String[] idStrings = ids.split(",");
		courierService.restoreBatch(idStrings);
		return SUCCESS;
	}
	
	
}
