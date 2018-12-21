package com.mycompany.faceedge.facemanageservice.FaceCloud;

import com.mycompany.faceedge.facemanageservice.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FaceCloudService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${FaceCloud.server}")
    private String faceCloudServer;

    public boolean updateOrderStatus(String orderID, int orderStatus) {
        String url = faceCloudServer + "/api/v1/updateOrderStatus?orderID={1}&status={2}";

        APIResponse response = restTemplate.getForEntity(url, APIResponse.class, orderID, orderStatus).getBody();

        if (response.getRetCode() == 1) {
            return true;
        }

        return false;
    }

}
