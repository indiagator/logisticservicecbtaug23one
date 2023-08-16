package com.cbt.logisticservicecbtaug23one;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FullRfq
{
    String logisticRfq;
    Port origin;
    Port destiniation;
    FullProductOffer offer;
}
