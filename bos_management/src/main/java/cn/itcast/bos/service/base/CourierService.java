package cn.itcast.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.Courier;

public interface CourierService {

	void save(Courier courier);

	Page<Courier> getPage(Pageable pageRequest, Specification<Courier> courierSpecification);

	void delBatch(String[] idStrings);

	void restoreBatch(String[] idStrings);

	List<Courier> findNoAssociation();

	

}
