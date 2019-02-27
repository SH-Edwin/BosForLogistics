package cn.itcast.bos.realm;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.PermissionService;
import cn.itcast.bos.service.system.RoleService;
import cn.itcast.bos.service.system.UserService;

//@Service("bosRealm")
public class BosRealm extends AuthorizingRealm  {

	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private PermissionService permissionService;
	
	@Override
	//授权
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
		SimpleAuthorizationInfo authorizationInfo=new SimpleAuthorizationInfo();
		//根据当前用户查询对应的角色和权限
		Subject subject=SecurityUtils.getSubject();
		User user = (User) subject.getPrincipal();
		//调用业务层,查询角色
		List<Role> roles=roleService.findRolesByUser(user);
		for (Role role : roles) {
			authorizationInfo.addRole(role.getKeyword());
		}
		//调用业务层,查询权限
		List<Permission> permissions=permissionService.findPermissionByUser(user);
		for (Permission permission : permissions) {
			authorizationInfo.addStringPermission(permission.getKeyword());
		}
		return authorizationInfo;
	}

	@Override
	//用户认证
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		
		//转化token
		UsernamePasswordToken usernamePasswordToken=(UsernamePasswordToken) token;
		//调用业务层根据用户名查询用户
		User user = userService.findByUsername(usernamePasswordToken.getUsername());
		if (user == null) {
			//用户名不存在
			return null;
		} else {
			//用户名存在
			//返回用户密码时,securityManager安全管理器,会自动比较返回密码和用户输入密码是否一致
			//如果密码一致则登陆成功,如果不一致则报错
			return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
		}

	}

}
