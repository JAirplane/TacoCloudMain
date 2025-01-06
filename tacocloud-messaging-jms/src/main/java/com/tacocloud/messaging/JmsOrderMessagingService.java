package com.tacocloud.messaging;

import com.tacocloud.domain.TacoOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class JmsOrderMessagingService implements OrderMessagingService {

//    @Value("${jms-destination}")
//    private String destination;

    private final JmsTemplate jmsTemplate;

    @Autowired
    public JmsOrderMessagingService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void sendOrder(TacoOrder order) {
        jmsTemplate.convertAndSend("tacocloud.order.queue", order);
    }
}
