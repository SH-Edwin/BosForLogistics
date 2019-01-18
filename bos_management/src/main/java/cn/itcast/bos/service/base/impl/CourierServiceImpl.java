package cn.itcast.bos.service.base.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.CourierRepository;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.service.base.CourierService;

@Service
@Transactional
public class CourierServiceImpl implements CourierService {
	
	@Autowired
	private CourierRepository courierRepository;
	
	@Override
	public void save(Courier courier) {
		courierRepository.save(courier);
	}


	@Override
	public Page<Courier> getPage(Pageable pageRequest, Specification<Courier> courierSpecification) {
		return courierRepository.findAll(courierSpecification, pageRequest);
	}


	@Override
	public void delBatch(String[] idStrings) {
		for (String idString : idStrings) {
			courierRepository.updateDelTag(Integer.parseInt(idString));
		}
	}


	@Override
	public void restoreBatch(String[] idStrings) {
		for (String idString : idStrings) {
			courierRepository.restoreBatch(Integer.parseInt(idString));
		}
	}



}
