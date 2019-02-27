package cn.itcast.bos.service.system.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.system.MenuRepository;
import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.MenuService;

@Service
@Transactional
public class MenuServiceImpl implements MenuService {

	@Autowired
	private MenuRepository menuRepository;
	
	@Override
	public List<Menu> findAll() {
		return menuRepository.findAll();
	}

	@Override
	public void save(Menu menu) {
		//如果没有选中父类菜单,则去除父类菜单对象
		if (menu.getParentMenu() != null && menu.getParentMenu().getId() == 0) {
			menu.setParentMenu(null);
		}
		menuRepository.save(menu);
	}

	@Override
	public Menu findOne(int id) {
		return menuRepository.findOne(id);
	}

	@Override
	public List<Menu> findByUser(User currentUser) {
		if (currentUser.getUsername().equals("admin")) {
			return menuRepository.findAll();
		}else {
			return menuRepository.findByUser(currentUser.getId());
		}
		
	}

}
