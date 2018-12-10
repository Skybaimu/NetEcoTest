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
        System.out.println("ヾ(◍°∇°◍)ﾉﾞ    启动成功      ヾ(◍°∇°◍)ﾉﾞ\n" +
                "___.                  __      _________                           \n" +
                "\\_ |__   ____   _____/  |_   /   _____/__ __   ____  ____   ______\n" +
                " | __ \\ /  _ \\ /  _ \\   __\\  \\_____  \\|  |  \\_/ ___\\/ ___\\ /  ___/\n" +
                " | \\_\\ (  <_> |  <_> )  |    /        \\  |  /\\  \\__\\  \\___ \\___ \\ \n" +
                " |___  /\\____/ \\____/|__|   /_______  /____/  \\___  >___  >____  >\n" +
                "     \\/                             \\/            \\/    \\/     \\/ ");


    }
}