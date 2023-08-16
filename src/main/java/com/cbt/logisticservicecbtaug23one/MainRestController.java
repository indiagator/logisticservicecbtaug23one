package com.cbt.logisticservicecbtaug23one;


import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1")
public class MainRestController
{
    @Autowired
    PortRepository portRepository;

    @Autowired
    OfferportlinkRepository offerportlinkRepository;

    @Autowired
    OrderportlinkRepository orderportlinkRepository;

    LogisticrfqRepository logisticrfqRepository;

    LogisticrfqofferRepository logisticrfqofferRepository;

    LogisticrfqorderRepository logisticrfqorderRepository;


    FullRfqService rfqService;

    Logger logger = LoggerFactory.getLogger(MainRestController.class);

    WebClient.Builder webClientBuilder;

    MainRestController(

            LogisticrfqRepository logisticrfqRepository,
            LogisticrfqofferRepository logisticrfqofferRepository,
            WebClient.Builder webClientBuilder,
            LogisticrfqorderRepository logisticrfqorderRepository,
            FullRfqService rfqService
    )
    {
        this.logisticrfqRepository = logisticrfqRepository;
        this.logisticrfqofferRepository = logisticrfqofferRepository;
        this.logisticrfqorderRepository = logisticrfqorderRepository;
        this.webClientBuilder = webClientBuilder;
        this.rfqService = rfqService;
    }

    @GetMapping("get/port/all")
    public List<Port> getAllPorts()
    {
        return portRepository.findAll();
    }

    @PostMapping("save/offer/port")
    public ResponseEntity<Offerportlink> saveOfferPort(@RequestBody Offerportlink offerportlink)
    {
        offerportlink.setId(String.valueOf((int)(Math.random()*100000)));
        offerportlinkRepository.save(offerportlink);
        return new ResponseEntity<>(offerportlink, HttpStatus.OK);
    }

    @PostMapping("save/order/port")
    public ResponseEntity<Orderportlink>  saveOrderPort(@RequestBody Orderportlink orderportlink)
    {
        orderportlink.setId(String.valueOf((int)(Math.random()*100000)));
        orderportlinkRepository.save(orderportlink);
        return new ResponseEntity<>(orderportlink,HttpStatus.OK);
    }

    @PostMapping("save/logistic/rfq")
    public ResponseEntity<Logisticrfq> saveLogisticRfq(@RequestBody Payment payment)
    {
        logger.info("NEW RFQ CREATED");
        Logisticrfq logisticrfq = new Logisticrfq();
        logisticrfq.setRfqid(String.valueOf((int)(Math.random()*100000)));
        logisticrfq.setOrderid(payment.getOrderid());
        logisticrfq.setOriginport(offerportlinkRepository.findByOfferid(payment.getOfferid()).get().getPortid());
        logisticrfq.setDestinationport(orderportlinkRepository.findByOrderid(payment.getOrderid()).get().getPortid());
        logisticrfq.setStatus("OPEN");
        logisticrfqRepository.save(logisticrfq);
        return new ResponseEntity<>(logisticrfq,HttpStatus.OK);
    }

    @GetMapping("get/logisticrfq/all")
    public List<FullRfq> getAllLogisticRfq()
    {
        return logisticrfqRepository.findAll().stream().
                filter(logisticrfq -> logisticrfq.getStatus().equals("OPEN")).
                map(logisticrfq -> rfqService.composeFullRfq(logisticrfq) ).collect(Collectors.toList());
    }

    @PostMapping("save/logisticrfq/offer")
    public ResponseEntity<Logisticrfqoffer> saveLogisticRfqOffer(@RequestBody Logisticrfqoffer logisticrfqoffer)
    {
        logisticrfqoffer.setRfqofferid(String.valueOf((int)(Math.random()*100000)));
        logisticrfqofferRepository.save(logisticrfqoffer);
        return new ResponseEntity<>(logisticrfqoffer, HttpStatus.OK);
    }

    @PostMapping("fulfil/logistic/order/{logisticorderid}")
    public Mono<String> fulfilOrder(@PathVariable("logisticorderid") String logorderid)
    {

        // SEND REQUEST TO PAYMENT_SERVICE FOR COMPLETING THE PAYMENTS

        return webClientBuilder.build().post().uri("http://localhost:8072/offer-service/api/v1/complete/payment/all/").
                body(Mono.just(logisticrfqorderRepository.findById(logorderid)),Logisticrfqorder.class).
                retrieve().bodyToMono(String.class);

    }

    @PostMapping("save/logisticrfq/order/{payertype}/{payername}")
    public ResponseEntity<Mono<Logisticpayment>> saveLogisticRfqOrder(@PathVariable("payertype") String payertype,
                                                                 @PathVariable("payername") String payername,
                                                                 @RequestBody Logisticrfqorder logisticrfqorder)
    {
        logisticrfqorder.setRfqorderid(String.valueOf((int)(Math.random()*100000)));
        logisticrfqorderRepository.save(logisticrfqorder);

        Logisticpayment logisticpayment = new Logisticpayment();
        logisticpayment.setId(String.valueOf((int)(Math.random()*100000)));
        logisticpayment.setRfqorderid(logisticrfqorder.getRfqorderid());
        logisticpayment.setPayertype(payertype);
        logisticpayment.setPayer(payername);
        logisticpayment.setStatus("DUE");
        logisticpayment.setPaymentwalletlink(null);

        logger.info("forwarding request to the payment-service");

        Mono<Logisticpayment> response = webClientBuilder.build().post().
                uri("http://localhost:8072/payment-service/api/v1/save/logistic/payment").
                body(Mono.just(logisticpayment),Logisticpayment.class).retrieve().bodyToMono(Logisticpayment.class);

        return new ResponseEntity<>(response, HttpStatus.OK); // THE RESPONSE HAS TO BE SENT BACK
    }

    @PostMapping("set/logisticrfq/order/status/{rfqorderid}/{payername}")
    public ResponseEntity<Logisticrfqorder> setLogisticRfqOrderStatus(@PathVariable("rfqorderid") String rfqOrderId ,
                                                                      @PathVariable("payername") String payername,
                                                                      @RequestParam String status,
                                                                      @RequestParam String payertype)
    {
        logisticrfqorderRepository.updateStatusByRfqorderid(status, rfqOrderId);
        if(status.equals("ACCEPTED"))
        {
            // send a request to the payment service for logistic payment creation
        }
        return new ResponseEntity<>(logisticrfqorderRepository.findById(rfqOrderId).get(),HttpStatus.OK);
    }

    @GetMapping("get/logisticrfq/offer/rfqwise/{rfqid}")
    public List<Logisticrfqoffer> getAllOffersRfqwise(@PathVariable String rfqid)
    {
       return logisticrfqofferRepository.findByRfqid(rfqid);
    }

}
