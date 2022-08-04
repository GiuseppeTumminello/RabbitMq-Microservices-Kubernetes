package com.acoustic.service;

import com.acoustic.entity.MicroservicesData;


public interface MessageListenerService extends Runnable{

    void run();
    MicroservicesData convertJsonToSicknessZusObject(String snsMessage);

}
