package cn.itcast.crm.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.crm.dao.CustomerRepository;
import cn.itcast.crm.domain.Customer;
import cn.itcast.crm.service.CustomerService;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Override
	public List<Customer> findNoAssociationCustomers() {
		return customerRepository.findByFixedAreaIdIsNull();
	}

	@Override
	public List<Customer> findAssociationCustomersByFixedArea(String fixedAreaId) {
		return customerRepository.findByFixedAreaId(fixedAreaId);
	}

	@Override
	public void associationCustomersToFixedArea(String customersIdStr, String fixedAreaId) {
		customerRepository.clearFixedAreaId(fixedAreaId);
		if (StringUtils.isBlank(customersIdStr)) {
			return;
		}
		String[] ids = customersIdStr.split(",");
		for (String idStr : ids) {
			int id = Integer.parseInt(idStr);
			customerRepository.associationCustomersToFixedArea(fixedAreaId, id);
		}
	}

	@Override
	public void regist(Customer customer) {
		customerRepository.save(customer);
	}

	@Override
	public Customer findCustomerByTelephone(String telephone) {
		return customerRepository.findByTelephone(telephone);
	}

	@Override
	public void active(String telephone) {
		customerRepository.active(telephone);
	}

	@Override
	public Customer login(String telephone, String password) {
		return customerRepository.findByTelephoneAndPassword(telephone, password);
	}

	@Override
	public String findFixedAreaIdByAddress(String address) {
		return customerRepository.findFixedAreaIdByAddress(address);
	}

}
