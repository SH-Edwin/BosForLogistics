package cn.itcast.bos.web.action.take_delivery;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
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

import cn.itcast.bos.domain.take_delivery.Promotion;
import cn.itcast.bos.service.take_delivery.PromotionService;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class PromotionAction extends ActionSupport implements ModelDriven<Promotion>{
	
	private Promotion promotion = new Promotion();
	
	@Override
	public Promotion getModel() {
		return promotion;
	}
	
	@Autowired
	private PromotionService promotionService;
	
	//保存
	private File titleImgFile;
	private String titleImgFileFileName;
	
	public void setTitleImgFile(File titleImgFile) {
		this.titleImgFile = titleImgFile;
	}

	public void setTitleImgFileFileName(String titleImgFileFileName) {
		this.titleImgFileFileName = titleImgFileFileName;
	}

	@Action(value="promotion_save", results= {@Result(name="success", type="redirect", location="./pages/take_delivery/promotion.html")})
	public String save() throws IOException {
		//保存宣传图片
		//图片保存的绝对路径
		String savePath = ServletActionContext.getServletContext().getRealPath("")+"/upload/";
		System.out.println(savePath);
		//图片保存的url
		String url = ServletActionContext.getRequest().getContextPath()+"/upload/";
		//图片的随机文件名
		UUID uuid = UUID.randomUUID();
		String ext = titleImgFileFileName.substring(titleImgFileFileName.lastIndexOf("."));
		String fileName = uuid+ext;
		//保存图片
		FileUtils.copyFile(titleImgFile, new File(savePath+fileName));
		//将图片的相对路径存入对象
		promotion.setTitleImg(url+fileName);
		//调用业务层保存对象
		promotionService.save(promotion);
		return SUCCESS;
	}
	
	//查询页面内容(不带条件)
	private Integer page;
	private Integer rows;
	
	public void setPage(Integer page) {
		this.page = page;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	@Action(value="promotion_pageQuery", results= {@Result(name="success", type="json")})
	public String pageQuery() {
		Pageable pageable=new PageRequest(page-1, rows);
		Page<Promotion> page =  promotionService.pageQuery(pageable);
		//返回total,rows
		Map<String , Object> map = new HashMap<String , Object>();
		map.put("total", page.getTotalElements());
		map.put("rows", page.getContent());
		ActionContext.getContext().getValueStack().push(map);
		return SUCCESS;
	}
}
