package com.cbt.logisticservicecbtaug23one;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "ports")
public class Port {
    @Id
    @Column(name = "portid", nullable = false, length = 10)
    private String portid;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "capacity", length = 10)
    private String capacity;

}