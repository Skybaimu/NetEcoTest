package com.yzsj.net.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Configuration
@Component
public  class ConfigUtil {
    @Value("${com.yzsj.resources.neteco.ip}")
    private String ip;

    @Value("${com.yzsj.resources.ipAddress}")
    private String ipAddress;

    @Value("${com.yzsj.resources.neteco.url}")
    private String url;

    @Value("${com.yzsj.resources.neteco.port}")
    private int port;

    @Value("${com.yzsj.resources.neteco.userid}")
    private String userid;

    @Value("${com.yzsj.resources.neteco.password}")
    private String password;

    @Value("${com.yzsj.resources.neteco.truststore.path}")
    private String truststorepath;

    @Value("${com.yzsj.resources.neteco.truststore.password}")
    private String truststorepassword;

    @Value("${com.yzsj.resources.neteco.keystore.path}")
    private String keystorepath;

    @Value("${com.yzsj.resources.neteco.keystore.password}")
    private String keystorepassword;


    public String getIp(){return ip;}

    public  String getUrl(){return url;}

    public int getPort() {
        return port;
    }

    public String getKeystorepassword() {
        return keystorepassword;
    }

    public String getPassword() {
        return password;
    }

    public String getUserid() {
        return userid;
    }

    public String getTruststorepath() {
        return truststorepath;
    }

    public String getTruststorepassword() {
        return truststorepassword;
    }

    public String getKeystorepath() {
        return keystorepath;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
