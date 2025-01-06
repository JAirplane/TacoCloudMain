package com.tacocloud.messaging;

import com.tacocloud.domain.TacoOrder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitOrderMessagingService implements OrderMessagingService {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitOrderMessagingService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendOrder(TacoOrder order) {
        rabbitTemplate.convertAndSend("tacocloud.orders", "order_key", order);
    }
}
