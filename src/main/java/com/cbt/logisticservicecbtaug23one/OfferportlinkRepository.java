package com.cbt.logisticservicecbtaug23one;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfferportlinkRepository extends JpaRepository<Offerportlink, String> {

    public Optional<Offerportlink> findByOfferid(String offerid);
}