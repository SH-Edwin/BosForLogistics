package cn.itcast.bos.service.base.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.CourierRepository;
import cn.itcast.bos.dao.base.FixedAreaRepository;
import cn.itcast.bos.dao.base.TakeTimeRepostitory;
import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.domain.base.TakeTime;
import cn.itcast.bos.service.base.FixedAreaService;

@Service
@Transactional
public class FixedAreaServiceImpl implements FixedAreaService {

	@Autowired
	private FixedAreaRepository fixedAreaRepository;
	
	@Autowired
	private CourierRepository courierRepository;
	
	@Autowired
	private TakeTimeRepostitory takeTimeRepostitory;
	
	@Override
	public void save(FixedArea fixedArea) {
		fixedAreaRepository.save(fixedArea);
	}
	
	@Override
	public Page<FixedArea> findPageData(Specification<FixedArea> specification, Pageable pageable) {
		return fixedAreaRepository.findAll(specification, pageable);
	}

	@Override
	public void associationCourierToFixedArea(String id, Integer courierId, Integer takeTimeId) {
		//使用持久态对象关联
		//得到各持久化对象
		FixedArea fixedArea=fixedAreaRepository.findOne(id);
		Courier courier=courierRepository.findOne(courierId);
		TakeTime takeTime=takeTimeRepostitory.findOne(takeTimeId);
		//关联
		fixedArea.getCouriers().add(courier);
		courier.setTakeTime(takeTime);
	}

	@Override
	public List<FixedArea> findAll() {
		return fixedAreaRepository.findAll();
	}

	@Override
	public FixedArea findById(String id) {
		return fixedAreaRepository.findOne(id);
	}

	
	
}
