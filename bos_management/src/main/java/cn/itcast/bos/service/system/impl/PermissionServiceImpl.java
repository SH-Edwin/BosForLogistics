package cn.itcast.bos.service.system.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.system.PermissionRepository;
import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.PermissionService;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService{

	@Autowired
	private PermissionRepository permissionRepository;
	
	@Override
	public List<Permission> findPermissionByUser(User user) {
		//如果是admin用户,授权所有权限
		if ("admin".equals(user.getUsername())) {
			return permissionRepository.findAll();
		} else {
			return permissionRepository.findByUser(user.getId());
		}
	}

	@Override
	public List<Permission> findAll() {
		return permissionRepository.findAll();
	}

	@Override
	public void save(Permission permission) {
		permissionRepository.save(permission);
	}

	@Override
	public Permission findById(int id) {
		return permissionRepository.findOne(id);
	}
	
	
}
