package cn.itcast.bos.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.itcast.bos.domain.base.FixedArea;

@Repository
public interface FixedAreaRepository extends JpaRepository<FixedArea, String>,JpaSpecificationExecutor<FixedArea>{

}
