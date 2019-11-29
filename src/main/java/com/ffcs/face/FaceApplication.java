package com.ffcs.face;

import com.ffcs.visionbigdata.mysql.service.UploadImageInfoService;
import com.ffcs.visionbigdata.mysql.service.impl.UploadImageInfoServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class FaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
    @Bean
    public UploadImageInfoService getUploadImageInfoService(){
        return new UploadImageInfoServiceImpl();
    }
}
