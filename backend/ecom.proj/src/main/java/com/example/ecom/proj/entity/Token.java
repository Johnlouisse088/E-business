package com.example.ecom.proj.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

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

    @Getter
    @Setter
    public boolean revoked;

    @Getter
    @Setter
    public boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)   // column name
    @OnDelete(action = OnDeleteAction.CASCADE)   // When you delete the user, it will automatically delete the tokens associated with it
    private User user;

    @CreationTimestamp             // auto-fill the field on insert:
    @Column(updatable = false)     // field should not be updated after it's first saved. (even you try to update)
    private LocalDateTime createdAt;

}
