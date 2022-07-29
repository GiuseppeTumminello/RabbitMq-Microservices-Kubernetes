package com.acoustic.service;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class CollectResponsesServiceImplementation {

    public static final int NUMBER_OF_CHECKS = 50;
    public static final int MICROSERVICE_COUNT = 10;


    //private final MicroservicesDataRepository microservicesDataRepository;

//    @Override
//    public void sendDataToMicroservices(BigDecimal grossMonthlySalary, UUID uuid) {
//       // this.rabbitTemplate.convertAndSend(this.rabbitMqSettings.getExchange(), this.rabbitMqSettings.getRoutingKey(), MicroservicesData.builder().amount(grossMonthlySalary).uuid(uuid).build());
//    }

//    @Override
//    public Map<String, BigDecimal> collectMicroservicesResponse(UUID uuid) {
//        Map<String, BigDecimal> response = new HashMap<>();
//        List<MicroservicesData2> data = new ArrayList<>();
//        var count = 0;
//        while (data.size() < MICROSERVICE_COUNT) {
//            data = this.microservicesDataRepository.findDataByUuid(uuid);
//            count++;
//            if (count == NUMBER_OF_CHECKS) {
//                break;
//            }
//        }
//        data.forEach(microservicesData -> response.put(microservicesData.getDescription(), microservicesData.getAmount()));
//        return response;
//    }
}
