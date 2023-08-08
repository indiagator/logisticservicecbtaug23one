package com.cbt.logisticservicecbtaug23one;


import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    PaymentRepository paymentRepository;

    LogisticrfqRepository logisticrfqRepository;

    Logger logger = LoggerFactory.getLogger(MainRestController.class);

    MainRestController(
            PaymentRepository paymentRepository,
            LogisticrfqRepository logisticrfqRepository
    )
    {
        this.paymentRepository = paymentRepository;
        this.logisticrfqRepository = logisticrfqRepository;
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
        logisticrfqRepository.save(logisticrfq);

        return new ResponseEntity<>(logisticrfq,HttpStatus.OK);

    }
}
