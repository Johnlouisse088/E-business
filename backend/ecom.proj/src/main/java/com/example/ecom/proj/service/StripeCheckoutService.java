package com.example.ecom.proj.service;

import com.example.ecom.proj.dao.ProductRepository;
import com.example.ecom.proj.dto.StripeResponse;
import com.example.ecom.proj.model.Product;
import com.stripe.Stripe;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StripeCheckoutService {

    @Value("${application.stripe.secret-key}")
    private String secretKey;

    @Autowired
    private ProductRepository productRepository;

    public StripeResponse checkoutProducts(Map<String, Object> productRequest) {
        Stripe.apiKey = secretKey;

        try {
            // 1. Extract the list of products from requestData
            // requestData.get("products") retrieves the value of "products" → This is already a List, but Java treats it as an Object because of Map<String, Object>.
            // Since we know it's a list, we cast it to List<Map<String, Object>>.
            List<Map<String, Object>> products = (List<Map<String, Object>>) productRequest.get("products");
            // Not correct approached
//            List<ProductRequest> products = (List<ProductRequest>) productRequest.get("products");
            System.out.println("------products: " + products);

            // 2. Create an empty list of Stripe Line Items
            List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

            // 3. Loop through the list of products
            for (Map<String, Object> product : products) {
                int productId = (int) product.get("id");      // Get id from the object
                Integer quantity = (Integer) product.get("stockQuantity");   // Get stockQuantity from the object

                // 4. Fetch the actual product details from the database
                Product dbProduct = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                // 5. Create a Stripe Line Item from product details
                SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd") // HARD CODED CURRENCY!!!
                                .setUnitAmount(dbProduct.getPrice().longValue()) // Price from database
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(dbProduct.getName()) // Product name from DB
                                        .build())
                                .build())
                        .setQuantity(quantity.longValue()) // Convert quantity to long
                        .build();

                // 6. Add the line item to the list
                lineItems.add(lineItem);
            }

            // 7. Create the Stripe Checkout Session
            SessionCreateParams params = SessionCreateParams.builder()
                    .addAllLineItem(lineItems)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:5173/stripe_checkout/success")
                    .setCancelUrl("http://localhost:5173/stripe_checkout/cancel")
                    .build();

            // 8. Create the session in Stripe
            Session session = Session.create(params);

            // 9. Return the session ID to the frontend
            return StripeResponse.builder()
                    .status("SUCCESS")
                    .message("Payment session created ")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return (StripeResponse) Map.of("error", "Error creating checkout session");
        }
    }
}
