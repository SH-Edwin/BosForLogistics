package cn.itcast.bos.web.action.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.base.StandardService;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class StandardAction extends ActionSupport implements ModelDriven<Standard>{
	//模型注入standard
	private Standard standard=new Standard();
	
	@Override
	public Standard getModel() {
		return standard;
	}
	
	//属性注入页面信息page,rows
	private Integer page;
	private Integer rows;

	public void setPage(Integer page) {
		this.page = page;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	@Autowired
	private StandardService standardService;
	
	//查询页面数据
	
	@Action(value = "standard_pageQuery", results = {@Result(name = "success", type = "json")})
	public String pageQuery() {
		//创建PageRequest对象(页码从0开始计数),需要传给dao查询当前页面信息
		Pageable pageRequest=new PageRequest(page-1, rows);
		Page<Standard> page=standardService.findPageData(pageRequest);
		//需要返回total总记录数,rows数据内容封装成json传到前端
		int total = page.getTotalPages();
		List<Standard> rows = page.getContent();
		Map<String, Object> resultMap=new HashMap<>();
		resultMap.put("total", total);
		resultMap.put("rows", rows);
		//将结果压入值栈,使用struts2-json-plugin,自动将值栈的顶部封装成json返回
		ActionContext.getContext().getValueStack().push(resultMap);
		return SUCCESS;
	}
	
	
	//添加标准
	@Action(value="standard_save",results= {@Result(name="success",type="redirect",location="./pages/base/standard.html")})
	public String save() {
		standardService.save(standard);
		return SUCCESS;
	}

}
