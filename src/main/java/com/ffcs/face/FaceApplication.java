package com.ffcs.face;


import com.ffcs.visionbigdata.fastdfs.FastdfsDownload;
import com.ffcs.visionbigdata.mysql.service.UploadImageInfoService;
import com.ffcs.visionbigdata.mysql.service.impl.UploadImageInfoServiceImpl;
import com.ffcs.visionbigdata.rabbitmq.Sender;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@SpringBootApplication
public class FaceApplication {
    private static final String CONF_NAME = "fdfs_client.conf";
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
    @Bean
    public Sender getSender(){
        return  new Sender();
    }

    @Bean
    public StorageClient getStorageClient() throws IOException, MyException {
        ClientGlobal.init(CONF_NAME);
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        return new StorageClient(trackerServer, storageServer);
    }
}

