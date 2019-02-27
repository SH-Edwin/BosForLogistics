package cn.itcast.bos.service.system.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ctc.wstx.util.StringUtil;

import cn.itcast.bos.dao.system.RoleRepository;
import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.MenuService;
import cn.itcast.bos.service.system.PermissionService;
import cn.itcast.bos.service.system.RoleService;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PermissionService permissionService;
	
	@Autowired
	private MenuService menuService;
	
	@Override
	public List<Role> findRolesByUser(User user) {
		//如果是admin用户,授权所有角色
		if ("admin".equals(user.getUsername())) {
			return roleRepository.findAll();
		} else {
			return roleRepository.findByUser(user.getId());
		}
	}

	@Override
	public List<Role> findAll() {
		return roleRepository.findAll();
	}

	@Override
	public void save(Role role, String[] permissionIds, String menuIds) {
		roleRepository.save(role);
		//关联permission
		if (permissionIds != null) {
			for (String id : permissionIds) {
				Permission permission = permissionService.findById(Integer.parseInt(id));
				role.getPermissions().add(permission);
			}
		}
		//关联menu
		if (StringUtils.isNotBlank(menuIds)) {
			String[] ids=menuIds.split(",");
			for (String id : ids) {
				Menu menu=menuService.findOne(Integer.parseInt(id));
				role.getMenus().add(menu);
			}
		}		
	}
	
	
}
