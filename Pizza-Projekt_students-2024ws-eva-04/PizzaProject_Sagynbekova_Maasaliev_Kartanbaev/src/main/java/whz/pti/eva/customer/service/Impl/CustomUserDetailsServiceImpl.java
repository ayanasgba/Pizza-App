package whz.pti.eva.customer.service.Impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import whz.pti.eva.customer.domain.CurrentUser;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.service.CustomUserDetailsService;
import whz.pti.eva.customer.service.CustomerService;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final CustomerService customerService;

    public CustomUserDetailsServiceImpl(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
        Customer customer = customerService.findByLoginName(loginName).orElseThrow(null);
        return new CurrentUser(customer);
    }

}