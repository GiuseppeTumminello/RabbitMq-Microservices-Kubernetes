package com.acoustic.SpringPolandSalaryCalculator.service;

import com.acoustic.entity.MicroservicesData2;
import com.acoustic.service.CollectResponsesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@SpringBootTest
@ActiveProfiles("test")
public class CollectResponseServiceTest {
    @MockBean
    private MicroservicesDataRepository microservicesDataRepository;
    @Autowired
    private CollectResponsesService collectResponsesService;


    @ParameterizedTest
    @CsvSource({"722.00, Total zus", "999.00, Annual net", "1250.33, Annual gross"})
    public void collectMicroservicesResponse(BigDecimal taxAmount, String description) {
        ArrayList<MicroservicesData2> microservicesDatum2s = new ArrayList<>();
        microservicesDatum2s.add(MicroservicesData2.builder().description(description).amount(taxAmount).build());
        given(this.microservicesDataRepository.findDataByUuid(any())).willReturn(microservicesDatum2s);
        Assertions.assertEquals(Map.of(description, taxAmount), this.collectResponsesService.collectMicroservicesResponse(UUID.randomUUID()));
        assertThat(this.collectResponsesService.collectMicroservicesResponse(UUID.randomUUID()))
                .isEqualTo(Map.of(description, taxAmount));

    }
}
