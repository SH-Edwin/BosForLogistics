package cn.itcast.crm.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import cn.itcast.crm.domain.Customer;

public interface CustomerService {

	//查询所有无关联定区的客户
	@Path("/noassociationcustomers")
	@GET
	@Produces({"application/xml","application/json"})
	public List<Customer> findNoAssociationCustomers();
	
	//查询此定区关联的客户
	@Path("/associationcustomers/{fixedAreaId}")
	@GET
	@Produces({"application/xml","application/json"})
	public List<Customer> findAssociationCustomersByFixedArea(@PathParam("fixedAreaId") String fixedAreaId);
	
	//将客户与定区进行关联
	@Path("/associationcustomerstofixedarea")
	@POST
	public void associationCustomersToFixedArea(
		@QueryParam("customersIdStr")	String customersIdStr, @QueryParam("fixedAreaId") String fixedAreaId);
}
