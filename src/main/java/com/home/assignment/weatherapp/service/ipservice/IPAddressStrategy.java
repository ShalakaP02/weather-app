package com.home.assignment.weatherapp.service.ipservice;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Component
public class IPAddressStrategy {
    public Optional<String> executeStrategy(IPAddressService ipAddressService){
        return ipAddressService.getIPAddress();
    }
}
