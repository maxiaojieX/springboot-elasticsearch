package com.example.demo.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by xiaojie.Ma on 2018/12/11.
 */
@Configuration
public class ElConfig {

    private static TransportClient staClient;

    @Bean
    public static TransportClient geTransportClient() throws UnknownHostException {
        if(staClient == null) {
            Settings settings = Settings.builder().build();
            TransportClient client=new PreBuiltTransportClient(settings);
            staClient = client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("39.105.12.38"),9300));
        }
        return staClient;
    }

}
