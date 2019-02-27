package cn.itcast.bos.web.action.system;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.UserService;
import cn.itcast.bos.utils.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class UserAction extends BaseAction<User>{
	
	//用户登陆
	@Action(value="user_login",results= {@Result(name="success",type="redirect",location="index.html"), @Result(name="login", type="redirect", location="login.html")})
	public String login() {
		//基于shiro实现登陆
		//首先,得到shiro的subject
		Subject currentUser=SecurityUtils.getSubject();
		//建立用户名和密码的认证令牌
		AuthenticationToken token=new UsernamePasswordToken(model.getUsername(), model.getPassword());
		//用户登陆
		try {
			currentUser.login(token);
			//登陆成功
			return SUCCESS;
		} catch (AuthenticationException e) {
			//登陆失败
			e.printStackTrace();
			return LOGIN;
		}
	}
	
	//用户登出
	@Action(value="user_logout", results= {@Result(name="success",type="redirect",location="login.html")})
	public String logout() {
		Subject currentUser=SecurityUtils.getSubject();
		currentUser.logout();
		return SUCCESS;
	}
	
	//用户列表
	@Autowired
	private UserService userService;
	
	@Action(value="user_list", results= {@Result(name="success", type="json")})
	public String list() {
		List<User> list = userService.findAll();
		ActionContext.getContext().getValueStack().push(list);
		return SUCCESS;
	}

	//保存用户
	private String[] roleIds;
	
	public void setRoleIds(String[] roleIds) {
		this.roleIds = roleIds;
	}

	@Action(value="user_save" , results= {@Result(name="success",type="redirect",location="pages/system/userinfo.html")})
	public String save() {
		userService.save(model, roleIds);
		return SUCCESS;
	}
}
