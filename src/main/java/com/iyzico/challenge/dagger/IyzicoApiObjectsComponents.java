package com.iyzico.challenge.dagger;

import com.iyzipay.Options;
import com.iyzipay.model.Buyer;
import com.iyzipay.request.CreatePaymentRequest;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = IyzicoApiObjectsModule.class)
public interface IyzicoApiObjectsComponents {
    CreatePaymentRequest provideCreatePaymentRequest();
    Options provideOptions();
    Buyer provideBuyer();
}
