package cn.itcast.bos.web.action.base;


import java.awt.Window.Type;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.service.base.AreaService;
import cn.itcast.bos.service.base.FixedAreaService;
import cn.itcast.bos.service.base.SubAreaService;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class SubAreaAction extends ActionSupport implements ModelDriven<SubArea>{
	
	private SubArea subArea=new SubArea();

	@Override
	public SubArea getModel() {
		return subArea;
	}
	
	@Autowired
	private SubAreaService subAreaService;
	@Autowired
	private FixedAreaService fixedAreaService;
	@Autowired
	private AreaService areaService;
	
	//添加,修改分区
	@Action(value="subArea_save", results= {@Result(name="success", type="redirect", location="./pages/base/sub_area.html")})
	public String save() {
		subAreaService.save(subArea);
		return SUCCESS;
	}
	
	//pageQuery
	//接收参数:page,rows,province,city,distinct, subArea:fixedArea.id,keyWords
	private Integer page;
	private Integer rows;
	
	public void setPage(Integer page) {
		this.page = page;
	}
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	
	@Action(value="subArea_pageQuery", results= {@Result(name="success", type="json")})
	public String pageQuery() {
		//pageable
		Pageable pageable=new PageRequest(page-1, rows);
		//specification
		Specification<SubArea> specification=new Specification<SubArea>() {
			@Override
			public Predicate toPredicate(Root<SubArea> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates =new ArrayList<Predicate>();
				//predicate
				//多表查询,查询area的province,city,distinct
				Join<SubArea, Area> areaJoin = root.join("area",JoinType.INNER);
				if (subArea.getArea()!=null && StringUtils.isNotBlank(subArea.getArea().getProvince())) {
					Predicate p1 = cb.like(areaJoin.get("province").as(String.class), "%"+subArea.getArea().getDistrict()+"%");
					predicates.add(p1);
				}
				if (subArea.getArea()!=null && StringUtils.isNotBlank(subArea.getArea().getCity())) {
					Predicate p2 = cb.like(areaJoin.get("city").as(String.class), "%"+subArea.getArea().getCity()+"%");
					predicates.add(p2);
				}
				if (subArea.getArea()!=null && StringUtils.isNotBlank(subArea.getArea().getDistrict())) {
					Predicate p3 = cb.like(areaJoin.get("district").as(String.class), "%"+subArea.getArea().getDistrict()+"%");
					predicates.add(p3);
				}
				//单表查询subArea:fixedArea.id,keyWords
				if (subArea.getFixedArea()!=null && StringUtils.isNotBlank(subArea.getFixedArea().getId())) {
					Predicate p4 = cb.equal(root.get("fixedArea").as(String.class), subArea.getFixedArea().getId());
					predicates.add(p4);
				}
				if (StringUtils.isNotBlank(subArea.getKeyWords())) {
					Predicate p5 = cb.like(root.get("keyWords").as(String.class), "%"+subArea.getKeyWords()+"%");
					predicates.add(p5);
				}
				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		Page<SubArea> page = subAreaService.pageQuery(pageable,specification);
		//封装total,rows传到前端
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("total", page.getTotalElements());
		map.put("rows", page.getContent());
		ActionContext.getContext().getValueStack().push(map);
		
		return SUCCESS;
	}
	
	//删除功能
	private String ids;
	public void setIds(String ids) {
		this.ids = ids;
	}
	@Action(value="subArea_delBatch", results= {@Result(name="success", type="redirect", location="./pages/base/sub_area.html")})
	public String delBatch() {
		subAreaService.delBatch(ids);
		return SUCCESS;
	}
	
	//批量导入
	private File file;
	private String fileFileName;

	public void setFile(File file) {
		this.file = file;
	}
	
	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}
	@Action(value="subArea_batchImport")
	public void batchImport() throws IOException {
		//存放批量的subArea的集合
		List<SubArea> list= new ArrayList<SubArea>();
		//workbook
		Workbook workbook=null;
		if (fileFileName.endsWith(".xls")) {
			workbook=new HSSFWorkbook(new FileInputStream(file));
		}else if (fileFileName.endsWith(".xlsx")) {
			workbook=new XSSFWorkbook(new FileInputStream(file));
		}
		//得到第一页
		Sheet sheet = workbook.getSheetAt(0);
		for (Row row : sheet) {
			//跳过第一行
			if (row.getRowNum() == 0) {
				continue;
			}
			//跳过空行
			if (row.getCell(0)==null||StringUtils.isBlank(row.getCell(0).getStringCellValue())) {
				continue;
			}
			//封装每行数据,存入集合
			SubArea subArea=new SubArea();
			subArea.setId(row.getCell(0).getStringCellValue());
			FixedArea fixedArea=fixedAreaService.findById(row.getCell(1).getStringCellValue());
			subArea.setFixedArea(fixedArea);
			Area area=areaService.findById(row.getCell(2).getStringCellValue());
			subArea.setArea(area);
			subArea.setKeyWords(row.getCell(3).getStringCellValue());
			subArea.setStartNum(row.getCell(4).getStringCellValue());
			subArea.setEndNum(row.getCell(5).getStringCellValue());
			subArea.setSingle(row.getCell(6).getStringCellValue().toCharArray()[0]);
			subArea.setAssistKeyWords(row.getCell(7).getStringCellValue());
			list.add(subArea);
		}
		subAreaService.saveBatch(list);
		ServletActionContext.getResponse().getWriter().print("1");
	}
	
}
