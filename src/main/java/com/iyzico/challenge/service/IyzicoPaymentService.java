package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Payment;
import com.iyzico.challenge.repository.PaymentRepository;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class IyzicoPaymentService {

    private final Logger logger = LoggerFactory.getLogger(IyzicoPaymentService.class);

    private final BankService bankService;
    private final PaymentRepository paymentRepository;
    private final HikariDataSource dataSource;

    public IyzicoPaymentService(BankService bankService, PaymentRepository paymentRepository, HikariDataSource dataSource) {
        this.bankService = bankService;
        this.paymentRepository = paymentRepository;
        this.dataSource = dataSource;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void pay(BigDecimal price) {
        //pay with bank
        long start = System.currentTimeMillis();
        BankPaymentRequest request = new BankPaymentRequest();
        request.setPrice(price);
        BankPaymentResponse response = bankService.pay(request);

        //insert records
        Payment payment = new Payment();
        payment.setBankResponse(response.getResultCode());
        payment.setPrice(price);
        dataSource.getHikariPoolMXBean().resumePool();
        paymentRepository.save(payment);

        long res = (System.currentTimeMillis()) - start;
        logger.info("Payment saved successfully!" + payment + " \t" + res);
    }
}
