package cn.itcast.bos.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cn.itcast.bos.domain.base.Standard;

@Repository
public interface StandardRepository extends JpaRepository<Standard, Integer> {
	
}
