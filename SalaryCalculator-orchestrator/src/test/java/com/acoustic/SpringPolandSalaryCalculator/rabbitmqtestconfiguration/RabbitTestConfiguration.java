package com.acoustic.SpringPolandSalaryCalculator.rabbitmqtestconfiguration;

import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.boot.test.context.TestConfiguration;

@RabbitListenerTest(capture = true)
@TestConfiguration
public class RabbitTestConfiguration {



}
