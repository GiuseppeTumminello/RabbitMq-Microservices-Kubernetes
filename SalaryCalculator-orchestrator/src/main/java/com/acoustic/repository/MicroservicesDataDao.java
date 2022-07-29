package com.acoustic.repository;


import com.acoustic.entity.MicroservicesData2;


public interface MicroservicesDataDao {

     MicroservicesData2 save(MicroservicesData2 microservicesData2);
     void getByName(String description);
}
