package com.acoustic.SpringPolandSalaryCalculator.rabbittest;

import com.acoustic.SpringPolandSalaryCalculator.rabbitmqtestconfiguration.RabbitTestConfiguration;
import com.acoustic.controller.SalaryCalculatorOrchestratorController;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = RabbitTestConfiguration.class)
public class RabbitMqSalaryCalculatorOrchestratorTest {
    @Autowired
    private SalaryCalculatorOrchestratorController salaryCalculatorOrchestratorController;

    @Autowired
    private RabbitListenerTestHarness harness;

    @Autowired
    private TestRabbitTemplate testRabbitTemplate;







}