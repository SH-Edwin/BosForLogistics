package cn.itcast.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.base.FixedArea;

public interface FixedAreaService {

	void save(FixedArea fixedArea);

	Page<FixedArea> findPageData(Specification<FixedArea> specification, Pageable pageable);

	void associationCourierToFixedArea(String id, Integer courierId, Integer takeTimeId);

	List<FixedArea> findAll();

	FixedArea findById(String id);

	

}
