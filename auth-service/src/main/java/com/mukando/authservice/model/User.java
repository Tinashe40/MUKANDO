package com.mukando.authservice.model;

import com.mukando.commons.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mukando_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    private String firstName;
    private String lastName;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    private String address;

    @Column(name = "city")
    private String city;

    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
