package com.acoustic.repository;


import com.acoustic.entity.MicroservicesData;

import java.util.List;


public interface MicroservicesDataDao {

     void save(MicroservicesData microservicesData);
     List<MicroservicesData> findByUuid(String description);
}
