package com.home.assignment.weatherapp.service.ipservice;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class IPAddressStrategy {
    public String executeStrategy(IPAddressService ipAddressService){
        return ipAddressService.getIPAddress();
    }
}
