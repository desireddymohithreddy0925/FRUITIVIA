package com.fruitivia.buyer;

import com.fruitivia.common.entity.BaseEntity;
import com.fruitivia.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "buyers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Buyer extends BaseEntity {

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "contact_email", nullable = false, unique = true)
    private String contactEmail;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column
    private String country;

    @Column
    private String address;

    @Column(name = "destination_city")
    private String destinationCity;

    @Column(name = "destination_port")
    private String destinationPort;

    @Column(name = "registration_information", columnDefinition = "TEXT")
    private String registrationInformation;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
