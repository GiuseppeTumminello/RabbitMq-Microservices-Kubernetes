package com.acoustic.repository;


import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.acoustic.entity.DisabilityZus;

@Repository
@EnableScan
public interface DisabilityZusRepository extends JpaRepository<DisabilityZus, String> {



}
