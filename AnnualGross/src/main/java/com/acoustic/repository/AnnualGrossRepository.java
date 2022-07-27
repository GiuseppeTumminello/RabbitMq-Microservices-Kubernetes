package com.acoustic.repository;

import com.acoustic.entity.AnnualGross;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableScan
public interface AnnualGrossRepository extends JpaRepository<AnnualGross, String> {
}
