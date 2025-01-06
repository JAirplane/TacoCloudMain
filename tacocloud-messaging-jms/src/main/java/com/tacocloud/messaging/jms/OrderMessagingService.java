package com.tacocloud.messaging.jms;

import com.tacocloud.domain.TacoOrder;

public interface OrderMessagingService {

    void sendOrder(TacoOrder order);
}
