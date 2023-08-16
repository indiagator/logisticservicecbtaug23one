package com.cbt.logisticservicecbtaug23one;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LogisticrfqRepository extends JpaRepository<Logisticrfq, String> {

    public Optional<List<Logisticrfq>> findAllByStatus(String status);

}