package cn.itcast.bos.dao.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.itcast.bos.domain.base.Area;

@Repository
public interface AreaRepository extends JpaRepository<Area, String>,JpaSpecificationExecutor<Area>{


}
