package com.ecom.ecommerce.customer;

import com.ecom.ecommerce.exception.CustomerNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.*;
import static java.util.Optional.*;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;

    public String createCustomer(@Valid CustomerRequest customerRequest) {
        var customer = customerRepository.save(mapper.toCustomer(customerRequest));
        return customer.getId();
    }

    public void updateCustomer(CustomerRequest customerRequest) {
        var customer = customerRepository.findById(customerRequest.id())
                .orElseThrow(() -> new CustomerNotFoundException(
                    format(" Cannot update customer:: No customer found with the provided ID:: %s", customerRequest.id())
                ));
        mergeCustomer(customer, customerRequest);
        customerRepository.save(mapper.toCustomer(customerRequest));
    }

    private void mergeCustomer(Customer customer, CustomerRequest request) {
        ofNullable(request.firstname())
                .filter(StringUtils::isNotBlank)
                .ifPresent(customer::setFirstname);
        ofNullable(request.lastname())
                .filter(StringUtils::isNotBlank)
                .ifPresent(customer::setLastname);
        ofNullable(request.email())
                .filter(StringUtils::isNotBlank)
                .ifPresent(customer::setEmail);

        if (request.address() == null) {
            customer.setEmail(request.email());
        }
    }

    public List<CustomerResponse> findAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(mapper::fromCustomer)
                .collect(Collectors.toList());
    }

    public Boolean existsById(String customerId) {
        return customerRepository.findById(customerId)
                .isPresent();
    }

    public CustomerResponse findById(String customerId) {
        return customerRepository.findById(customerId)
                .map(mapper::fromCustomer)
                .orElseThrow(() -> new CustomerNotFoundException(format("Customer not found with the given ID %s", customerId)));
    }

    public void deleteCustomer(String customerId) {
        customerRepository.deleteById(customerId);
    }
}
