package com.mycompany.faceedge.facemanageservice.api;


import com.mycompany.faceedge.facemanageservice.APIResponse;
import com.mycompany.faceedge.facemanageservice.FaceCloud.FaceCloudService;
import com.mycompany.faceedge.facemanageservice.FaceRecognition.FaceRecognitionService;
import com.mycompany.faceedge.facemanageservice.Order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    FaceCloudService faceCloudService;

    @Autowired
    FaceRecognitionService faceRecognitionService;


    //只有单程票才会调用这个接口
    @RequestMapping(value = "/api/v2/updateOrderStatus", method = RequestMethod.GET)
    public APIResponse updateOrderStatus(@RequestParam("orderID") String orderID,
                                         @RequestParam("status") int status) {


        APIResponse response = new APIResponse();


        // 单程票: 进站成功 / 出站成功
        // 时间是不是要使用设备端的？
        int ret = orderService.updateOrderStatus(orderID, status);
        if (status == 1) {
            if (ret == 1) {

                    System.out.println("进站成功 " + orderID);
            } else {
                System.out.println("进站失败 " + orderID);
            }
        } else if (status == 2) {
            if (ret == 1) {

                System.out.println("出站成功 " + orderID);
            } else {
                System.out.println("出站失败 " + orderID);
            }
        }



        if (ret == 1) {
            response.setRetCode(1);
            response.setRetMsg("success");

            //删除人脸库
            String groupID = "";
            if (status == 1) //已进站
                groupID = "single_enter";
            else if (status == 2) // 已出站
                groupID = "single_exit";

            boolean bRet = faceRecognitionService.deleteFace(groupID, orderID);
            if (bRet) {
                System.out.println("删除人脸成功, 组" + groupID + "，订单" + orderID);
            } else {
                System.out.println("删除人脸失败, 组" + groupID + "，订单" + orderID);
            }

            // 更新远程订单状态
            if (status == 1) {
                bRet = faceCloudService.singleEntered(orderID, status);
            } else if (status == 2) {
                bRet = faceCloudService.singleExited(orderID, status);
            }

            if (bRet) {
                System.out.println("云端订单状态更新成功" + orderID);
            } else {
                System.out.println("云端订单状态更新失败" + orderID);
            }

            //
        } else {
            response.setRetCode(0);
            response.setRetMsg("error");
        }

        return response;
    }
}
