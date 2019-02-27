package cn.itcast.bos.service.system;

import java.util.List;

import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.domain.system.User;

public interface PermissionService {

	List<Permission> findPermissionByUser(User user);

	List<Permission> findAll();

	void save(Permission model);

	Permission findById(int id);


}
