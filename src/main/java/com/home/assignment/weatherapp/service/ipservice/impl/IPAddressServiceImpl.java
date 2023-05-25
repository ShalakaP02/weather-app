package com.home.assignment.weatherapp.service.ipservice.impl;

import com.home.assignment.weatherapp.service.ipservice.IPAddressService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;


public class IPAddressServiceImpl implements IPAddressService {
    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR" };

    HttpServletRequest httpServletRequest;

    public IPAddressServiceImpl(){}

    public IPAddressServiceImpl(HttpServletRequest httpServletRequest){
        this.httpServletRequest=httpServletRequest;
    }

    @Override
    public Optional<String> getIPAddress() {
        return getClientIpAddress(httpServletRequest);
    }


    private Optional<String> getClientIpAddress(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return Optional.of(ip);
            }
        }
        return Optional.of(request.getRemoteAddr());
    }
}
