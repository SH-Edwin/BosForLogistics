package cn.itcast.bos.service.system;

import java.util.List;

import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;

public interface RoleService {

	List<Role> findRolesByUser(User user);

	List<Role> findAll();

	void save(Role model, String[] permissionIds, String menuIds);

}
