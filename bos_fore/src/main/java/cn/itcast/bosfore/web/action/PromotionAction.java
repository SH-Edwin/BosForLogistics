package cn.itcast.bosfore.web.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import cn.itcast.bos.domain.take_delivery.PageBean;
import cn.itcast.bos.domain.take_delivery.Promotion;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class PromotionAction extends ActionSupport {

	private int page;
	private int rows;
	private int id;

	public void setId(int id) {
		this.id = id;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	// 显示列表
	@Action(value = "promotion_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {

		// 基于后端的webservice获得活动列表的数据
		@SuppressWarnings("unchecked")
		PageBean<Promotion> pageBean = WebClient
				.create("http://localhost:8080/bos_management/services/promotionService/pageQuery?page=" + page
						+ "&rows=" + rows)
				.accept(MediaType.APPLICATION_JSON).get(PageBean.class);

		ActionContext.getContext().getValueStack().push(pageBean);

		return SUCCESS;
	}

	// 显示详细页面
	@Action(value = "promotion_showDetail")
	public String showDetail() throws IOException, TemplateException {
		// 判断id对应html是否存在
		String htmlRealPath = ServletActionContext.getServletContext().getRealPath("/freemarker");
		File htmlFile = new File(htmlRealPath + "/" + id + ".html");
		// 如果不存在则基于freemarker创建
		if (!htmlFile.exists()) {
			// 创建configuration,生成模板实例
			Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
			configuration.setDirectoryForTemplateLoading(
					new File(ServletActionContext.getServletContext().getRealPath("/WEB-INF/freemarker_templates")));
			// 使用指定模板生成template
			Template template = configuration.getTemplate("promotion_detail.ftl");
			// 填充数据模型-map
			Promotion promotion = WebClient.create("http://localhost:8080/bos_management/services/promotionService/promotionDetail/" + id)
					.accept(MediaType.APPLICATION_JSON).get(Promotion.class);
			Map<String, Object> map = new HashMap<String , Object>();
			map.put("promotion", promotion);
			//完成数据合并,生成文件
			template.process(map, new OutputStreamWriter(new FileOutputStream(htmlFile), "utf-8"));
			
		}
		// 如果存在html文件,则直接返回文件
		ServletActionContext.getResponse().setContentType("text/html;charset=utf-8");
		FileUtils.copyFile(htmlFile, ServletActionContext.getResponse().getOutputStream());
		return NONE;
	}
}
