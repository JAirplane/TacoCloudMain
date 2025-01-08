package com.tacocloud.messaging;

import com.tacocloud.domain.TacoOrder;

public interface OrderMessagingService {

    void sendOrder(TacoOrder order);
}
