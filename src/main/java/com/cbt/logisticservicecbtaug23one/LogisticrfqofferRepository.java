package com.cbt.logisticservicecbtaug23one;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogisticrfqofferRepository extends JpaRepository<Logisticrfqoffer, String> {
    List<Logisticrfqoffer> findByRfqid(String rfqid);


}