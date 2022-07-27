package com.acoustic.repository;


import com.acoustic.entity.Health;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableScan
public interface HealthRepository extends JpaRepository<Health, String> {



}
