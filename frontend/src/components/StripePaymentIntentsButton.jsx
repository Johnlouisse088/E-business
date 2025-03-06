import React, { useState } from "react";
import { loadStripe } from "@stripe/stripe-js";
import { Elements, useStripe, useElements, CardElement } from "@stripe/react-stripe-js";
import axios from "axios";
import { Button } from "react-bootstrap"; // Using Bootstrap Button


// Your Stripe Publishable Key (replace with your actual key)
const stripePromise = loadStripe('pk_test_51QypgUGgyaJLBFz0S6Kr0dqRm7cWFZ5Ooo005Aq5EKgiECAwPjD32qVNmHmfSRjsCMqF9FCexU0POEynWaNSvVc600XdNBPlHQ');


// ✅ Custom component to wrap button inside `<Elements>`
const StripePaymentIntentsButton = ({ cartItems }) => {

    console.log("----cartItems: ", cartItems);
    const stripe = useStripe();
    const elements = useElements();
    const [isProcessing, setIsProcessing] = useState(false);
    const [error, setError] = useState(null);

    const handleStripeCheckout = async () => {
        if (!stripe || !elements) {
            console.error("Stripe has not loaded yet.");
            return;
        }

        setIsProcessing(true);

        try {
            // Remove imageUrl field
            const filteredData = cartItems?.map(({ imageUrl, ...rest }) => rest);

            // Fetch the clientSecret from the backend only when needed
            const { data } = await axios.post('http://localhost:8080/api/product/v1/checkout', {
                products: filteredData,
            });
            console.log("--data: ", data);
            // Get the secret key from the response
            const clientSecret = data.clientSecret;

            // Get card details
            const cardElement = elements.getElement(CardElement);

            // Confirm the payment with the client secret
            const { error: paymentError, paymentIntent } = await stripe.confirmCardPayment(clientSecret, {
                payment_method: {
                    card: cardElement,
                },
            });

            if (paymentError) {
                setError(paymentError.message);
            } else if (paymentIntent.status === 'succeeded') {
                alert("Payment Successful!");
            }

        } catch (error) {
            console.error("Error fetching client secret:", error);
            setError("Something went wrong.");
        }

        setIsProcessing(false);
    };

    return (
        <div>
            {/* <CardElement /> ✅ Stripe Card Input */}
            <Button variant="primary" onClick={handleStripeCheckout} disabled={isProcessing}>
                {isProcessing ? "Processing..." : "Stripe Purchase"}
            </Button>
            {error && <div style={{ color: "red" }}>{error}</div>}
        </div>
    );
};

// ✅ Wrap the new component inside `<Elements>`
const StripeButton = ({ cartItems }) => (
    <Elements stripe={stripePromise}>
        <StripeCheckoutButton cartItems={cartItems} />
    </Elements>
);

export default StripeButton;
