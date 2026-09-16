package com.fruitivia.supplier;

import com.fruitivia.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends BaseEntity {

    @Column(name = "supplier_code", unique = true, nullable = false)
    private String supplierCode;

    @Column(nullable = false)
    private String name;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column
    private String phone;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column
    private String address;

    @Column
    private String region;

    @Column(name = "farm_location")
    private String farmLocation;

    @Column(name = "farming_information", columnDefinition = "TEXT")
    private String farmingInformation;
}
