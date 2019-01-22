package cn.itcast.crm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cn.itcast.crm.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer>{
	
	public List<Customer> findByFixedAreaIdIsNull();
	
	public List<Customer> findByFixedAreaId(String fixedAreaId);
	
	@Query("update Customer set fixedAreaId=? where id=?")
	@Modifying
	public void associationCustomersToFixedArea(String fixedAreaId, Integer id);
	
	@Query("update Customer set fixedAreaId=null where fixedAreaId=?")
	@Modifying
	public void clearFixedAreaId(String fixedAreaId);
}
