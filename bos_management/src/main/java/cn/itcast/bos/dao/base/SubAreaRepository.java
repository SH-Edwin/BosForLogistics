package cn.itcast.bos.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.itcast.bos.domain.base.SubArea;

@Repository
public interface SubAreaRepository extends JpaRepository<SubArea, String>, JpaSpecificationExecutor<SubArea> {

}
