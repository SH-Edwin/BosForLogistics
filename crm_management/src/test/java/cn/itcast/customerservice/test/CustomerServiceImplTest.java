package cn.itcast.customerservice.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.itcast.crm.service.CustomerService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class CustomerServiceImplTest {
	
	@Autowired
	private CustomerService customerService;

	@Test
	public void testFindNoAssociationCustomers() {
		System.out.println(customerService.findNoAssociationCustomers());
	}

	@Test
	public void testFindAssociationCustomersByFixedArea() {
		System.out.println(customerService.findAssociationCustomersByFixedArea("pdxq001"));
	}

	@Test
	public void testAssociationCustomersToFixedArea() {
		customerService.associationCustomersToFixedArea("1,2", "pdxq001");
	}

}
