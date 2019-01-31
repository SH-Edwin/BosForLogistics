package cn.itcast.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.SubArea;

public interface SubAreaService {

	void save(SubArea subArea);

	Page<SubArea> pageQuery(Pageable pageable, Specification<SubArea> specification);

	void delBatch(String ids);

	void saveBatch(List<SubArea> list);

}
