package whz.pti.eva.base.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import whz.pti.eva.base.domain.PaymentRequest;
import whz.pti.eva.customer.domain.Account;

import java.math.BigDecimal;

@Service
public class PaymentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    public PaymentServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean checkBalance(Long customerId, BigDecimal amount) {
        String url = paymentServiceUrl + "/balance/check";
        PaymentRequest request = new PaymentRequest(customerId, amount);
        ResponseEntity<Boolean> response = restTemplate.postForEntity(url, request, Boolean.class);
        return Boolean.TRUE.equals(response.getBody());
    }

    public void chargeCustomer(Long customerId, BigDecimal amount) {
        String url = paymentServiceUrl + "/process";

        PaymentRequest request = new PaymentRequest(customerId, amount);
        ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Failed to charge customer through payment service.");
        }
    }

    public void createPaymentAccount(Long customerId) {
        String url = paymentServiceUrl + "/accounts/create?customerId=" + customerId;
        try {
            restTemplate.postForObject(url, null, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create payment account for customer ID: " + customerId, e);
        }
    }

    public Account getAccountByCustomerId(Long customerId) {
        String url = paymentServiceUrl + "/accounts/" + customerId;

        try {
            return restTemplate.getForObject(url, Account.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("Account not found for customer ID: " + customerId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch account from MPA", e);
        }
    }

    public String addFundsToAccount(Long customerId, BigDecimal amount) {
        String url = paymentServiceUrl + "/addFunds";

        PaymentRequest request = new PaymentRequest();
        request.setCustomerId(customerId);
        request.setAmount(amount);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }
}

