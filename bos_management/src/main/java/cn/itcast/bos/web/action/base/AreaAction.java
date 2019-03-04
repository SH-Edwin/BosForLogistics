package cn.itcast.bos.web.action.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.service.base.AreaService;
import cn.itcast.bos.utils.PinYin4jUtils;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class AreaAction extends ActionSupport implements ModelDriven<Area>{
	//模型驱动
	private Area area=new Area();
	@Override
	public Area getModel() {
		return area;
	}
	
	//属性接收page,rows
	private Integer page;
	private Integer rows;
	public void setPage(Integer page) {
		this.page = page;
	}
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	
	@Autowired
	private AreaService areaService;
	
	//查询page对象
	@Action(value="area_pageQuery",results= {@Result(name="success",type="json")})
	public String pageQuery() {
		//页码信息
		Pageable pageable=new PageRequest(page-1, rows);
		//条件规则对象
		Specification<Area> specification=new Specification<Area>() {
			
			@Override
			public Predicate toPredicate(Root<Area> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				//断言集合
				List<Predicate> predicates=new ArrayList<Predicate>();
				//断言判断增加约束
				if (StringUtils.isNotBlank(area.getProvince())) {
					Predicate p1 = cb.like(root.get("province").as(String.class), "%"+area.getProvince()+"%");
					predicates.add(p1);
				}
				if (StringUtils.isNotBlank(area.getCity())) {
					Predicate p2 = cb.like(root.get("city"), "%"+area.getCity()+"%");
					predicates.add(p2);
				}
				if (StringUtils.isNotBlank(area.getDistrict())) {
					Predicate p3 = cb.like(root.get("district"), "%"+area.getDistrict()+"%");
					predicates.add(p3);
				}
				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		Page<Area> page=areaService.pageQuery(specification,pageable);
		Map<String, Object> pageMap=new HashMap<String, Object>();
		pageMap.put("total", page.getTotalElements());
		pageMap.put("rows", page.getContent());
		ActionContext.getContext().getValueStack().push(pageMap);
		return SUCCESS;
	}
	
	//添加+修改
	@Action(value="area_save",results= {@Result(name="success",type="redirect",location="./pages/base/area.html")})
	public String save() {
		areaService.save(area);
		return SUCCESS;
	}
	
	private String ids;
	
	public void setIds(String ids) {
		this.ids = ids;
	}
	//删除
	@Action(value="area_delBatch" , results= {@Result(name="success",type="redirect",location="./pages/base/area.html")})
	public String delBatch() {
		String[] idStrings = ids.split(",");
		areaService.delBatch(idStrings);
		return SUCCESS;
	}
	
	//批量导入
	//接收上传的文件和文件名
	private File file;
	private String fileFileName;
	public void setFile(File file) {
		this.file = file;
	}
	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}
	@Action(value="area_batchImport")
	public void batchImport() throws FileNotFoundException, IOException {
		List<Area> areas=new ArrayList<Area>();
		Workbook workbook=null;
		//判断文件拓展名,创建解析文件,加载excle
		if (fileFileName.endsWith(".xls")) {
			workbook=new HSSFWorkbook(new FileInputStream(file));
		}else if (fileFileName.endsWith(".xlsx")) {
			workbook=new XSSFWorkbook(new FileInputStream(file));
		}
		//解析顺序,sheet,row,cell,锁定sheet页,遍历行,获得行的每列
		//读取第一个sheet
		Sheet sheet = workbook.getSheetAt(0);
		//读取sheet的每一行
		for (Row row : sheet) {
			//跳过第一行
			if (row.getRowNum()==0) {
				continue;
			}
			//跳过空行
			if (row.getCell(0)==null||StringUtils.isBlank(row.getCell(0).getStringCellValue())) {
				continue;
			}
			//封装每行的area对象,存入集合
			Area area = new Area();
			area.setId(row.getCell(0).getStringCellValue());
			area.setProvince(row.getCell(1).getStringCellValue());
			area.setCity(row.getCell(2).getStringCellValue());
			area.setDistrict(row.getCell(3).getStringCellValue());
			area.setPostcode(row.getCell(4).getStringCellValue());
			//城市编码和简码,城市编码:城市拼音;简码:省市区的拼音头字母拼接
			String province=area.getProvince();
			String city=area.getCity();
			String district=area.getDistrict();
			//去掉最后一个字"省,市,区"
			province=province.substring(0, province.length()-1);
			city=city.substring(0, city.length()-1);
			district=district.substring(0, district.length()-1);
			//城市编码
			String cityCode = PinYin4jUtils.hanziToPinyin(city);
			area.setCitycode(cityCode);
			//简码
			String[] headArray = PinYin4jUtils.getHeadByString(province+city+district);
			StringBuffer stringBuffer=new StringBuffer();
			for (String head : headArray) {
				stringBuffer.append(head);
			}
			area.setShortcode(stringBuffer.toString());
			//把area存入集合
			areas.add(area);
		}
		//调用业务层将集合存入数据库
		areaService.saveBatch(areas);
		ServletActionContext.getResponse().getWriter().print("1");
	}
	
	//显示所有区域
	@Action(value="area_findAll" , results= {@Result(name="success", type="json")})
	public String findAll() {
		List<Area> list = areaService.findAll();
		ActionContext.getContext().getValueStack().push(list);
		return SUCCESS;
	}

	
}
