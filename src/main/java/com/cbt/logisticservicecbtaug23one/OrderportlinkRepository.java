package com.cbt.logisticservicecbtaug23one;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderportlinkRepository extends JpaRepository<Orderportlink, String> {

    public Optional<Orderportlink> findByOrderid(String orderid);
}