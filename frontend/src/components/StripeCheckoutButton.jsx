import React from "react";
import axios from "axios";

import { Button } from 'react-bootstrap';

const StripeCheckoutButton = ({ cartItems }) => {
  const handleCheckout = async () => {
    try {
      // Call backend to create a Checkout Session
      const { data } = await axios.post("http://localhost:8080/api/product/v1/checkout", {
        products: cartItems
      });

      // Redirect to Stripe Checkout Page
      window.location.href = data.sessionUrl;
    } catch (error) {
      console.error("Error creating checkout session:", error);
    }
  };

  return (
    <Button variant="primary" onClick={handleCheckout}>Proceed to Checkout</Button>
  );
};

export default StripeCheckoutButton;
