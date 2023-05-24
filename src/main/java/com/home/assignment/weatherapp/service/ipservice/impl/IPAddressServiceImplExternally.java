package com.home.assignment.weatherapp.service.ipservice.impl;

import com.home.assignment.weatherapp.service.ipservice.IPAddressService;
import org.springframework.web.client.RestTemplate;

public class IPAddressServiceImplExternally  implements IPAddressService {

    private RestTemplate restTemplate;
    private String apiUrl;

    public IPAddressServiceImplExternally(){}

    public IPAddressServiceImplExternally(RestTemplate restTemplate,String apiUrl){
        this.restTemplate=restTemplate;
        this.apiUrl=apiUrl;
    }

    @Override
    public String getIPAddress() {
        String ipAddress = restTemplate.getForObject(apiUrl, String.class);
        return ipAddress;
    }


}

