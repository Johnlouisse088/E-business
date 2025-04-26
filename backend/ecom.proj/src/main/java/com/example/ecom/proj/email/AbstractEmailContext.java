package com.example.ecom.proj.email;

import com.example.ecom.proj.entity.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
public abstract class AbstractEmailContext {
    private String to;          // Recipient email
    private String from;        // Sender email
    private String subject;     // Email subject
    private String email;
    private String templateLocation;  // Template file to use for the body
    private Map<String, Object> context;  // Dynamic values injected into the template

    // ✅ Explicit constructor to ensure initialization
    public AbstractEmailContext() {
        this.context = new HashMap<>();
    }

    public abstract <T> void init(User user);

    // Adds a key-value pair to the context (dynamic data for the template)
    // intern() saves memory by reusing identical strings
    public Object put(String key, Object value) {
        return key == null ? null : this.context.put(key.intern(), value);
    }

    public Map<String, Object> getContext() {
        return context;
    }


}
