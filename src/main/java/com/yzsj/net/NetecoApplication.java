package com.yzsj.net;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.yzsj.net.config.WMK;

@SpringBootApplication
@EnableConfigurationProperties({WMK.class})  
public class NetecoApplication {
    public static void main(String[] args) {
        SpringApplication.run(NetecoApplication.class, args);
    }
}