//package com.example.ecom.proj.servicev1;
//
//import com.example.ecom.proj.dto.ProductRequest;
//import com.example.ecom.proj.dto.StripeResponse;
//import com.stripe.Stripe;
//import com.stripe.exception.StripeException;
//import com.stripe.param.checkout.SessionCreateParams;
//import com.stripe.model.checkout.Session;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class StripeServicev1 {
//
//    @Value("${stripe.secretKey}")
//    private String secretKey;
//
//    public StripeResponse checkoutProducts(ProductRequest productRequest) {
//        Stripe.apiKey=secretKey;
//
//        // Create a PaymentIntent with the order amount and currency
//        SessionCreateParams.LineItem.PriceData.ProductData productData =
//                SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                        .setName(productRequest.getName())
//                        .build();
//
//        // Create new line item with the above product data and associated price
//        SessionCreateParams.LineItem.PriceData priceData =
//                SessionCreateParams.LineItem.PriceData.builder()
//                        // If there's no currency indicated, it will convert to usd as a default currency
//                        .setCurrency(productRequest.getCurrency() != null ? productRequest.getCurrency() : "USD")
//                        // Take note, when receiving a 'price' field came from the frontend, it is equivalent to cents
//                        // eg. 100 cents (request) -> 1 dollar (stripe interpreted)
//                        .setUnitAmount(productRequest.getPrice().longValue())
//                        .setProductData(productData)
//                        .build();
//
//        // Create new line item with the above price data
//        SessionCreateParams.LineItem lineItem =
//                SessionCreateParams
//                        .LineItem.builder()
//                        .setQuantity((long) productRequest.getStockQuantity())
//                        .setPriceData(priceData)
//                        .build();
//
//        // Create new session with the line items
//        SessionCreateParams params =
//                SessionCreateParams.builder()
//                        .setMode(SessionCreateParams.Mode.PAYMENT)
//                        .setSuccessUrl("http://localhost:8080/success")
//                        .setCancelUrl("http://localhost:8080/cancel")
//                        .addLineItem(lineItem)
//                        .build();
//
//        // Create new session
//        Session session = null;
//
//        try {
//            session = Session.create(params);
//        } catch (StripeException e) {
//            throw new RuntimeException(e);
//        }
//
//        return StripeResponse.builder()  // getting red underline in builder()
//                .status("SUCCESS")
//                .message("Payment session created ")
//                .sessionId(session.getId())
//                .sessionUrl(session.getUrl())
//                .build();
//    }
//}
