package cn.itcast.bos.service.base.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.SubAreaRepository;
import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.service.base.SubAreaService;

@Service
@Transactional
public class SubAreaServiceImpl implements SubAreaService {
	
	@Autowired
	private SubAreaRepository subAreaRepository;
	
	@Override
	public void save(SubArea subArea) {
		subAreaRepository.save(subArea);
	}

	@Override
	public Page<SubArea> pageQuery(Pageable pageable, Specification<SubArea> specification) {
		return subAreaRepository.findAll(specification, pageable);
	}

	@Override
	public void delBatch(String ids) {
		String[] idArray = ids.split(",");
		for (String id : idArray) {
			subAreaRepository.delete(id);
		}
	}

	@Override
	public void saveBatch(List<SubArea> list) {
		subAreaRepository.save(list);
	}

}
