package com.fruitivia.buyer;

import com.fruitivia.user.Address;
import com.fruitivia.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "buyers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Buyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String companyName;

    private String contactPhone;

    private String taxIdentificationNumber;

    private String defaultCurrency;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_address_id", referencedColumnName = "id")
    private Address billingAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_address_id", referencedColumnName = "id")
    private Address shippingAddress;
}
