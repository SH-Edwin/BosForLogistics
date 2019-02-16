package cn.itcast.crm.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import cn.itcast.crm.domain.Customer;

public interface CustomerService {
	//服务路径:
	//http://localhost:9002/crm_management/services/customerService/

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
	
	//客户注册
	@Path("/regist")
	@POST
	@Consumes({"application/xml","application/json"})
	public void regist(Customer customer);
	
	//根据电话查询客户
	@Path("/customer/findByTelephone/{telephone}")
	@GET
	@Produces({"application/xml","application/json"})
	public Customer findCustomerByTelephone(@PathParam("telephone") String telephone);
	
	//激活用户
	@Path("/customer/active/{telephone}")
	@PUT
	@Consumes({"application/xml","application/json"})
	public void active(@PathParam("telephone") String telephone);
	
	//用户登陆
	@Path("/customer/login")
	@GET
	@Produces({"application/xml","application/json"})
	public Customer login(@QueryParam("telephone")String telephone, @QueryParam("password")String password);
	
	//根据地址查询定区
	@Path("/findFixedAreaIdByAddress")
	@GET
	@Produces({"application/xml","application/json"})
	public String findFixedAreaIdByAddress(@QueryParam("address")String address);
}
