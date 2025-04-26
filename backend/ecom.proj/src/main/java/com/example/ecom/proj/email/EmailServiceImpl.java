package com.example.ecom.proj.email;

import jakarta.mail.MessagingException;

public interface EmailServiceImpl {
    void sendMail(final AbstractEmailContext email) throws MessagingException;
}
