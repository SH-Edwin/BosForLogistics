package cn.itcast.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.Area;

public interface AreaService {

	void save(Area area);

	void delBatch(String[] idStrings);

	void saveBatch(List<Area> areas);

	Page<Area> pageQuery(Specification<Area> specification, Pageable pageable);

}
