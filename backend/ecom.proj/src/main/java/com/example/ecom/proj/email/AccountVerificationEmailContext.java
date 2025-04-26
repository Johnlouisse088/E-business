package com.example.ecom.proj.email;

import com.example.ecom.proj.constant.SecurityConstants;
import com.example.ecom.proj.entity.User;

import org.springframework.web.util.UriComponentsBuilder;

public class AccountVerificationEmailContext extends AbstractEmailContext{
    private String token;

    @Override
    public <T> void init(User user) {
        put("firstName", user.getFirstname());
        setTemplateLocation("mailing/email-verification");
//        setTemplateLocation("templates/mailing/email-verification");
        setSubject("Test email subject");
        setFrom("noreply@yourapp.com");
        setTo(user.getEmail());
    }

    public void setToken(String token) {
        this.token = token;
        put("token", token);
    }

    // Dynamically builds the verification link like: https://yourdomain.com/register/verify?token=abc123
    // It should be an frontend not the backend endpoint
    public void buildVerificationUrl(final String baseURL, String token) {
        final String url = UriComponentsBuilder.fromHttpUrl(baseURL)
                .path(SecurityConstants.EMAIL_VERIFICATION_FULL_URL).queryParam("token", token).toUriString();
        put("verificationURL", url);
    }
}
