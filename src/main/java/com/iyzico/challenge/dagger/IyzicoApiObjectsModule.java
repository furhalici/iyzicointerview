package com.iyzico.challenge.dagger;

import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreatePaymentRequest;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class IyzicoApiObjectsModule {

    @Provides
    CreatePaymentRequest getCreatePaymentRequest(){
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setLocale(Locale.TR.getValue());
        request.setConversationId("123456789");
        request.setCurrency(Currency.TRY.name());
        request.setInstallment(1);
        request.setBasketId("B67832");
        request.setPaymentChannel(PaymentChannel.WEB.name());
        request.setPaymentGroup(PaymentGroup.PRODUCT.name());

        request.setPaymentCard(getPaymentCard());
        request.setBuyer(getBuyer());
        request.setShippingAddress(getShippingAddress());
        request.setBillingAddress(getBillingAddress());

        return request;
    }

    @Provides
    @Singleton
    Options getOptions(){
        Options options = new Options();
        options.setApiKey("sandbox-NvO0yfMS2Y9G76oGmYxx3MgUPBzvygIQ");
        options.setSecretKey("sandbox-1bTERdHgd7X1FEM9S5816DUaXWxmxCcb");
        options.setBaseUrl("https://sandbox-api.iyzipay.com");
        return options;
    }

    PaymentCard getPaymentCard(){
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardHolderName("John Doe");
        paymentCard.setCardNumber("5528790000000008");
        paymentCard.setExpireMonth("12");
        paymentCard.setExpireYear("2030");
        paymentCard.setCvc("123");
        paymentCard.setRegisterCard(0);
        return paymentCard;
    }

    @Provides
    Buyer getBuyer(){
        Buyer buyer = new Buyer();
        buyer.setId("BY789");
        buyer.setName("Furkan");
        buyer.setSurname("H.");
        buyer.setGsmNumber("+905350000000");
        buyer.setEmail("email@email.com");
        buyer.setIdentityNumber("74300864791");
        buyer.setLastLoginDate("2015-10-05 12:43:35");
        buyer.setRegistrationDate("2013-04-21 15:12:09");
        buyer.setRegistrationAddress("Nidakule Göztepe, Merdivenköy Mah. Bora Sok. No:1");
        buyer.setIp("85.34.78.112");
        buyer.setCity("Istanbul");
        buyer.setCountry("Turkey");
        buyer.setZipCode("34732");
        return buyer;
    }

    Address getShippingAddress(){
        Address shippingAddress = new Address();
        shippingAddress.setContactName("Jane Doe");
        shippingAddress.setCity("Istanbul");
        shippingAddress.setCountry("Turkey");
        shippingAddress.setAddress("Nidakule Göztepe, Merdivenköy Mah. Bora Sok. No:1");
        shippingAddress.setZipCode("34742");
        return shippingAddress;
    }

    Address getBillingAddress(){
        Address billingAddress = new Address();
        billingAddress.setContactName("Jane Doe");
        billingAddress.setCity("Istanbul");
        billingAddress.setCountry("Turkey");
        billingAddress.setAddress("Nidakule Göztepe, Merdivenköy Mah. Bora Sok. No:1");
        billingAddress.setZipCode("34742");
        return billingAddress;
    }
}
