package com.fruitivia.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;
    
    @Column(nullable = false)
    private String city;
    
    private String state;
    
    @Column(nullable = false)
    private String country;
    
    private String zipCode;
}
