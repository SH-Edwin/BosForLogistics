package cn.itcast.bos.web.action.take_delivery;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.service.take_delivery.WayBillService;
import cn.itcast.bos.utils.FileUtils;

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
	
	//导出Excel报表
	@Action(value="wayBill_exportXls")
	public String exportXls() throws IOException {
		//查询满足条件的数据
		List<WayBill> wayBills = wayBillService.findWayBills(wayBill);
		//生成文件----
		//创建xls文件
		HSSFWorkbook workbook = new HSSFWorkbook();
		//sheet页
		HSSFSheet sheet = workbook.createSheet("运单信息");
		//表头
		HSSFRow headRow = sheet.createRow(0);
		headRow.createCell(0).setCellValue("运单号");
		headRow.createCell(1).setCellValue("寄件人");
		headRow.createCell(2).setCellValue("寄件人电话");
		headRow.createCell(3).setCellValue("寄件人地址");
		headRow.createCell(4).setCellValue("收件人");
		headRow.createCell(5).setCellValue("收件人电话");
		headRow.createCell(6).setCellValue("收件人地址");
		//内容
		for (WayBill wayBill : wayBills) {
			HSSFRow dataRow = sheet.createRow(sheet.getLastRowNum()+1);
			dataRow.createCell(0).setCellValue(wayBill.getWayBillNum());
			dataRow.createCell(1).setCellValue(wayBill.getSendName());
			dataRow.createCell(2).setCellValue(wayBill.getSendMobile());
			dataRow.createCell(3).setCellValue(wayBill.getSendAddress());
			dataRow.createCell(4).setCellValue(wayBill.getRecName());
			dataRow.createCell(5).setCellValue(wayBill.getRecMobile());
			dataRow.createCell(6).setCellValue(wayBill.getRecAddress());
		}
		
		//下载导出------
		//设置头信息
		//设置内容类型
		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		String filename="运单信息.xls";
		String agent = ServletActionContext.getRequest().getHeader("user-agent");
		//文件名针对浏览器转码
		filename = FileUtils.encodeDownloadFilename(filename, agent);
		//内容设置为附件
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment;filename="+filename);
		
		//获得输出流
		ServletOutputStream outputStream = ServletActionContext.getResponse().getOutputStream();
		//向输出流写入文件
		workbook.write(outputStream);
		
		//关闭文件
		workbook.close();

		return NONE;
	}
	
	//导出PDF报表
	@Action(value="wayBill_exportPdf")
	public String exportPdf() throws IOException, DocumentException {
		//查询满足条件的数据
		List<WayBill> wayBills = wayBillService.findWayBills(wayBill);
		
		//设置头信息
		//设置内容类型
		ServletActionContext.getResponse().setContentType("application/pdf");
		String filename="运单信息.pdf";
		String agent = ServletActionContext.getRequest().getHeader("user-agent");
		//文件名针对浏览器转码
		filename = FileUtils.encodeDownloadFilename(filename, agent);
		//内容设置为附件
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment;filename="+filename);
		
		//获得输出流
		ServletOutputStream outputStream = ServletActionContext.getResponse().getOutputStream();
		
		//生成文件----
		//生成PDF文件
		Document document = new Document();
		PdfWriter.getInstance(document, outputStream);
		document.open();
		
		//向PDF文件写内容
		Table table = new Table(7);//创建一个7列的表格
		table.setWidth(120);//宽度
		table.setBorder(1);//边框
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER); // 水平对齐方式
		table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP); // 垂直对齐方式
		// 设置表格字体
		BaseFont cn = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
		Font font = new Font(cn, 10, Font.NORMAL, Color.BLUE);
		
		// 写表头
		table.addCell(buildCell("运单号", font));
		table.addCell(buildCell("寄件人", font));
		table.addCell(buildCell("寄件人电话", font));
		table.addCell(buildCell("寄件人地址", font));
		table.addCell(buildCell("收件人", font));
		table.addCell(buildCell("收件人电话", font));
		table.addCell(buildCell("收件人地址", font));
		// 写数据
		for (WayBill wayBill : wayBills) {
			table.addCell(buildCell(wayBill.getWayBillNum(), font));
			table.addCell(buildCell(wayBill.getSendName(), font));
			table.addCell(buildCell(wayBill.getSendMobile(), font));
			table.addCell(buildCell(wayBill.getSendAddress(), font));
			table.addCell(buildCell(wayBill.getRecName(), font));
			table.addCell(buildCell(wayBill.getRecMobile(), font));
			table.addCell(buildCell(wayBill.getRecAddress(), font));
		}
		// 将表格加入文档
		document.add(table);
		document.close();
		return NONE;
	}
	//itext填写格子的方法
	private Cell buildCell(String content, Font font) throws BadElementException {
		Phrase phrase = new Phrase(content, font);
		return new Cell(phrase);
	}
	
}
