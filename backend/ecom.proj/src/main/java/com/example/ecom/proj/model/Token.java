package com.example.ecom.proj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="token")
public class Token {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    public String token;

    public boolean revoked;

    public boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)   // column name
    @OnDelete(action = OnDeleteAction.CASCADE)   // When you delete the user, it will automatically delete the tokens associated with it
    private User user;
}
