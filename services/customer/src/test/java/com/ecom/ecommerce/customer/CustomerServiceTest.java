package com.ecom.ecommerce.customer;

import com.ecom.ecommerce.exception.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class) // Enables Mockito support for JUnit 5
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository; // Mocked dependency

    @Mock
    private CustomerMapper mapper; // Mocked dependency

    @InjectMocks
    private CustomerService customerService; // Service under test

    private CustomerRequest customerRequest;
    private Customer customer;
    private CustomerResponse customerResponse;

    @BeforeEach
    void setUp() {
        // Initialize test data
        customerRequest = new CustomerRequest(UUID.randomUUID().toString(), "John", "Doe", "john.doe@example.com", new Address("code", "13", "59000"));
        customer = new Customer();
        customer.setId(customerRequest.id());
        customer.setFirstname(customerRequest.firstname());
        customer.setLastname(customerRequest.lastname());
        customer.setEmail(customerRequest.email());

        customerResponse = new CustomerResponse(customer.getId(), customer.getFirstname(), customer.getLastname(), customer.getEmail(), customerRequest.address());
    }

    @Test
    void testCreateCustomer() {
        // Mock the behavior of mapper and repository
        when(mapper.toCustomer(customerRequest)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);

        // Call the method and verify results
        String customerId = customerService.createCustomer(customerRequest);

        assertNotNull(customerId);
        assertEquals(customerRequest.id(), customerId);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testCreateCustomer_InvalidInput() {
        // Test creating a customer with invalid input
        CustomerRequest invalidRequest = new CustomerRequest("", "", "", "", new Address());
        assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(invalidRequest));
    }

    @Test
    void testUpdateCustomer_CustomerExists() {
        // Simulate customer exists scenario
        when(customerRepository.findById(customerRequest.id())).thenReturn(Optional.of(customer));
        when(mapper.toCustomer(customerRequest)).thenReturn(customer);

        // Ensure no exception is thrown and customer is updated
        assertDoesNotThrow(() -> customerService.updateCustomer(customerRequest));
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testUpdateCustomer_PartialUpdate() {
        // Test updating a customer with only some fields
        CustomerRequest partialRequest = new CustomerRequest(customer.getId(), "NewFirstName", null, null, null);
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        customerService.updateCustomer(partialRequest);
        assertEquals("NewFirstName", customer.getFirstname());
    }

    @Test
    void testUpdateCustomer_CustomerNotFound() {
        // Simulate customer not found scenario
        when(customerRepository.findById(customerRequest.id())).thenReturn(Optional.empty());

        // Expect exception to be thrown
        Exception exception = assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(customerRequest));
        assertTrue(exception.getMessage().contains("No customer found with the provided ID"));
    }

    @Test
    void testFindAllCustomers() {
        // Simulate retrieving all customers
        when(customerRepository.findAll())
                .thenReturn(List.of(customer));
        when(mapper.fromCustomer(customer))
                .thenReturn(customerResponse);

        // Call the method and verify results
        List<CustomerResponse> customers = customerService.findAllCustomers();
        assertEquals(1, customers.size());
        assertEquals(customerResponse, customers.get(0));
    }

    @Test
    void testFindAllCustomers_EmptyList() {
        // Test retrieving customers when repository is empty
        when(customerRepository.findAll()).thenReturn(List.of());
        List<CustomerResponse> customers = customerService.findAllCustomers();
        assertTrue(customers.isEmpty());
    }

    @Test
    void testExistsById_CustomerExists() {
        // Simulate customer exists scenario
        when(customerRepository.findById(customerRequest.id())).thenReturn(Optional.of(customer));

        // Verify that the method returns true
        assertTrue(customerService.existsById(customerRequest.id()));
    }

    @Test
    void testExistsById_CustomerDoesNotExist() {
        // Simulate customer does not exist scenario
        when(customerRepository.findById(customerRequest.id())).thenReturn(Optional.empty());

        // Verify that the method returns false
        assertFalse(customerService.existsById(customerRequest.id()));
    }
}
