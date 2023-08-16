package com.cbt.logisticservicecbtaug23one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class FullRfqService
{


    Logger logger = LoggerFactory.getLogger(MainRestController.class);


    PortRepository portRepository;

    WebClient.Builder webClientBuilder;

    OrderRepository orderRepository;

    FullRfqService(
            PortRepository portRepository,
            WebClient.Builder webClientBuilder,
            OrderRepository orderRepository
    )
    {
        this.portRepository = portRepository;
        this.webClientBuilder = webClientBuilder;
        this.orderRepository = orderRepository;
    }

    public FullRfq composeFullRfq(Logisticrfq rfq)
    {
        String offerid = orderRepository.findById(rfq.getOrderid()).get().getOfferid();

        FullRfq fullRfq = new FullRfq();
        fullRfq.setLogisticRfq(rfq.getRfqid());
        fullRfq.setOrigin(portRepository.findById(rfq.getOriginport()).get());
        fullRfq.setDestiniation(portRepository.findById(rfq.getDestinationport()).get());
        logger.info("sending request to offer-service for full product offer composition");
        fullRfq.setOffer(

                webClientBuilder.build().get().uri("http://localhost:8072/offer-service/api/v1/get/product/offer/".
                        concat(offerid)).retrieve().bodyToMono(FullProductOffer.class).block()
        );

        return fullRfq;

    }
}
