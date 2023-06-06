package com.home.assignment.weatherapp.service.ipService;

import com.home.assignment.weatherapp.service.ipservice.IPAddressService;
import com.home.assignment.weatherapp.service.ipservice.IPAddressStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class IPStrategyTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    IPAddressService iPAddressService;

    @InjectMocks
    IPAddressStrategy ipAddressStrategy;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void executeStrategyTest(){

        when(restTemplate.getForObject(anyString(),any())).thenReturn("103.208.69.70");
        Optional<String> ip = Optional.of("103.208.69.70");
        when(iPAddressService.getIPAddress()).thenReturn(ip);

        Optional<String> resIp = ipAddressStrategy.executeStrategy(iPAddressService);

        assertNotNull(resIp.get());
        assertEquals("103.208.69.70",resIp.get());

    }
}
