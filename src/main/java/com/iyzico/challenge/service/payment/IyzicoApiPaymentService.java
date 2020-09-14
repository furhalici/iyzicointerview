package com.iyzico.challenge.service.payment;

import com.iyzico.challenge.dagger.DaggerIyzicoApiObjectsComponents;
import com.iyzico.challenge.entity.Payment;
import com.iyzico.challenge.exception.IyzicoPaymentException;
import com.iyzico.challenge.repository.PaymentRepository;
import com.iyzipay.Options;
import com.iyzipay.model.*;

import com.iyzipay.request.CreatePaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class IyzicoApiPaymentService {

    private final PaymentRepository paymentRepository;
    private final Logger logger = LoggerFactory.getLogger(IyzicoApiPaymentService.class);
    protected Options options;

    public IyzicoApiPaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        options = DaggerIyzicoApiObjectsComponents.create().provideOptions();
    }

    public void pay(CreatePaymentRequest request) throws IyzicoPaymentException {
        com.iyzipay.model.Payment iyzipayPayment = com.iyzipay.model.Payment.create(request, options);

        if (Status.FAILURE.getValue().equals(iyzipayPayment.getStatus())) {
            IyzicoPaymentException iyzicoPaymentException = new IyzicoPaymentException(
                    iyzipayPayment.getErrorCode(),
                    iyzipayPayment.getErrorMessage(),
                    iyzipayPayment.getErrorGroup());
            logger.error("Payment failure", iyzicoPaymentException);
            throw iyzicoPaymentException;
        }

        Payment payment = new Payment();
        payment.setBankResponse("200");
        payment.setPrice(iyzipayPayment.getPaidPrice());
        paymentRepository.saveAndFlush(payment);

        logger.info("Payment success\tBuyer:{} {}\tPrice: {}",
                request.getBuyer().getName(),
                request.getBuyer().getSurname(),
                request.getPaidPrice());
    }
}
