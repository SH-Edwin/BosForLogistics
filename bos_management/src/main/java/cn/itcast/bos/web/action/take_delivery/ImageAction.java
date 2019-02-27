package cn.itcast.bos.web.action.take_delivery;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class ImageAction extends ActionSupport{
	
	private File imgFile;
	private String imgFileFileName;
	
	public void setImgFile(File imgFile) {
		this.imgFile = imgFile;
	}


	public void setImgFileFileName(String imgFileFileName) {
		this.imgFileFileName = imgFileFileName;
	}


	//图片上传
	@Action(value="image_upload" , results= {@Result(name="success",type="json")})
	public String upload() throws IOException {
		//保存图片的绝对路径
		String realPath = ServletActionContext.getServletContext().getRealPath("/upload");
		//保存图片的url
		String url = ServletActionContext.getRequest().getContextPath()+"/upload/";
		
		//随机文件名
		UUID randomUUID = UUID.randomUUID();
		//文件拓展名
		String ext = imgFileFileName.substring(imgFileFileName.lastIndexOf("."));
		//保存的文件名
		String fileName = randomUUID+ext;
		//保存图片
		FileUtils.copyFile(imgFile, new File(realPath+"/"+fileName));
		
		//返回客户端信息:error:0/url:
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("error", 0);
		result.put("url", url+fileName);
		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS;
	}
	
	//团片管理
	@Action(value="image_manage", results= {@Result(name="success",type="json")})
	public String manage() {
		//根目录路径，可以指定绝对路径，比如 /var/www/attached/
		String rootPath = ServletActionContext.getServletContext().getRealPath("/") + "upload/";
		//根目录URL，可以指定绝对路径，比如 http://www.yoursite.com/attached/
		String rootUrl  = ServletActionContext.getRequest().getContextPath() + "/upload/";
		
		//遍历当前目录取的文件信息
		List<Map<String,Object>> fileList = new ArrayList<Map<String,Object>>();
		File currentPathFile = new File(rootPath);
		String[] fileTypes = new String[]{"gif", "jpg", "jpeg", "png", "bmp"};
		if(currentPathFile.listFiles() != null) {
			for (File file : currentPathFile.listFiles()) {
				Map<String, Object> hash = new HashMap<String, Object>();
				String fileName = file.getName();
				if(file.isDirectory()) {
					hash.put("is_dir", true);
					hash.put("has_file", (file.listFiles() != null));
					hash.put("filesize", 0L);
					hash.put("is_photo", false);
					hash.put("filetype", "");
				} else if(file.isFile()){
					String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
					hash.put("is_dir", false);
					hash.put("has_file", false);
					hash.put("filesize", file.length());
					hash.put("is_photo", Arrays.<String>asList(fileTypes).contains(fileExt));
					hash.put("filetype", fileExt);
				}
				hash.put("filename", fileName);
				hash.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified()));
				fileList.add(hash);
			}
		}
		//存入json
		Map<String, Object> result =new HashMap<String, Object>();
		result.put("moveup_dir_path", "");
		result.put("current_dir_path", rootPath);
		result.put("current_url", rootUrl);
		result.put("total_count", fileList.size());
		result.put("file_list", fileList);
		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS;
	}
}
