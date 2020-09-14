package com.iyzico.challenge.controller;

import com.iyzico.challenge.exception.IyzicoPaymentException;
import com.iyzico.challenge.exception.ProductStockException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(value = ProductStockException.class)
    @ResponseBody
    public ResponseEntity<Map<String,Object>> exceptionProductStockException(ProductStockException e) {
        return ResponseEntity.badRequest().body(createResponseBody("ProductStockException",e.getMessage()));
    }

    @ExceptionHandler(value = IyzicoPaymentException.class)
    @ResponseBody
    public ResponseEntity<?> exceptionIyzicoPaymentException(IyzicoPaymentException e) {
        return ResponseEntity.badRequest().body(createResponseBody("IyzicoPaymentException",e.getMessage()));
    }

    private Map<String,Object> createResponseBody(String type, String msg){
        Map<String,Object> response = new HashMap<>();
        response.put("type",type);
        response.put("msg",msg);
        response.put("timestamp",System.currentTimeMillis()/1000);
        return response;
    }
}
