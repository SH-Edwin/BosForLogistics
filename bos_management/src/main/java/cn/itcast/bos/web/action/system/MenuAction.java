package cn.itcast.bos.web.action.system;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.MenuService;
import cn.itcast.bos.utils.BaseAction;


@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class MenuAction extends BaseAction<Menu>{
	
	@Autowired
	private MenuService menuService;
	
	//查询菜单列表
	@Action(value="menu_list", results= {@Result(name="success", type="json")})
	public String list() {
		List<Menu> list = menuService.findAll();
		ActionContext.getContext().getValueStack().push(list);
		return SUCCESS;
	}
	
	//添加菜单
	@Action(value="menu_save",results= {@Result(name="success", type="redirect", location="pages/system/menu.html")})
	public String save() {
		menuService.save(model);
		return SUCCESS;
	}
	
	//按用户查询菜单
	@Action(value="menu_showmenu",  results= {@Result(name="success", type="json")})
	public String showMenu() {
		//得到当前用户
		Subject subject = SecurityUtils.getSubject();
		User currentUser=(User) subject.getPrincipal();
		//查询当前用户菜单
		List<Menu> list = menuService.findByUser(currentUser);
		ActionContext.getContext().getValueStack().push(list);
		return SUCCESS;
	}
}
