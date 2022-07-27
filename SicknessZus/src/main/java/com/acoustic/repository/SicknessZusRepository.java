package com.acoustic.repository;


import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.acoustic.entity.SicknessZus;

@Repository
@EnableScan
public interface SicknessZusRepository extends JpaRepository<SicknessZus, String> {



}
