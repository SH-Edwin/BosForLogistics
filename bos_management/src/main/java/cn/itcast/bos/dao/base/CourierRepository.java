package cn.itcast.bos.dao.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cn.itcast.bos.domain.base.Courier;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Integer>,JpaSpecificationExecutor<Courier> {
	
	@Query(value="update Courier set deltag='1' where id=?")
	@Modifying
	public void updateDelTag(int id);
	
	@Query(value="update Courier set deltag=null where id=?")
	@Modifying
	public void restoreBatch(int parseInt);
	

}
