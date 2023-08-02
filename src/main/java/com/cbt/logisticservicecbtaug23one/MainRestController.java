package com.cbt.logisticservicecbtaug23one;


import com.netflix.discovery.converters.Auto;
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
}
