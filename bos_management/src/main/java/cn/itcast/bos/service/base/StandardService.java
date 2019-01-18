package cn.itcast.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cn.itcast.bos.domain.base.Standard;

public interface StandardService {

	void save(Standard standard);

	Page<Standard> findPageData(Pageable pageRequest);

	List<Standard> findAll();

	

}
