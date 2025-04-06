package whz.pti.eva.customer.service;

import java.util.Optional;

import whz.pti.eva.customer.domain.Account;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.request.CustomerAddressRequest;
import whz.pti.eva.base.service.BaseService;

public interface CustomerService extends BaseService<Customer> {
    
	Customer create(CustomerAddressRequest customerAddressRequest);

	Optional<Customer> findByLoginName(String loginName);

	boolean deleteById(Long id);

 	void addFunds(Account account);
}