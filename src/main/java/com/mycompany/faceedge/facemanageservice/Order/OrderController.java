package com.mycompany.faceedge.facemanageservice.Order;


import com.mycompany.faceedge.facemanageservice.APIResponse;
import com.mycompany.faceedge.facemanageservice.FaceCloud.FaceCloudService;
import com.mycompany.faceedge.facemanageservice.FaceRecognition.FaceRecognitionService;
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



    @RequestMapping(value="/api/v1/updateOrderStatus", method= RequestMethod.GET)
    public APIResponse updateOrderStatus(@RequestParam("orderID") String orderID,
                                         @RequestParam("status") int status) {


        APIResponse response = new APIResponse();



        // 更新本地订单状态
        int ret = orderService.updateOrderStatus(orderID, status);



        if (ret == 1) {
            response.setRetCode(1);
            response.setRetMsg("success");

            //删除人脸库
            boolean bRet = faceRecognitionService.deleteFace(orderID);
            if (bRet) {
                System.out.println("人脸识别服务：更新订单状态成功");
            } else {
                System.out.println("人脸识别服务：更新订单状态成功");
            }

            // 更新远程订单状态
            bRet = faceCloudService.updateOrderStatus(orderID, status);
            if (bRet) {
                System.out.println("人脸云服务：更新订单状态成功");
            } else {
                System.out.println("人脸云服务：更新订单状态成功");
            }

            //
        } else {
            response.setRetCode(0);
            response.setRetMsg("error");
        }

        return response;
    }



    @RequestMapping(value="/api/v1/order", method= RequestMethod.POST)
    public APIResponse insert(@RequestBody Order order) {

        APIResponse response = new APIResponse();
        response.setRetCode(0);
        response.setRetMsg("error");



        if (order.getUserType() == 2 || order.getUserType() == 3) {
            // 只有单程票用户才会生成订单
        } else {
            response.setRetMsg("用户类型的值必须是2或者3");
            return response;
        }

        int ret = orderService.insert(order);

        //要在交易日志表插入记录

        if (ret == 1) {

            response.setRetCode(1);
            response.setRetMsg("success");
            return response;
        } else {

            return response;
        }

    }


    @RequestMapping(value="/api/v1/order", method= RequestMethod.GET)
    public List<Order> getAll() {
        return orderService.getAll();


    }

    @RequestMapping(value="/api/v1/syncOrder", method= RequestMethod.GET)
    public List<Order> syncOrder(@RequestParam("tenantID") String tenantID,
                                 @RequestParam("direction") int direction,
                                 @RequestParam("enterStationLineCode") String enterStationLineCode,
                                 @RequestParam("enterStationCode") String enterStationCode,
                                 @RequestParam("exitStationLineCode") String exitStationLineCode,
                                 @RequestParam("exitStationCode") String exitStationCode,
                                 @RequestParam("updateTime") String updateTime) {

//一体机获取设备对应线路，对应站点的订单
        // 只返回当日单程票订单

      //  if (updateTime == "") {
         //   DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
       //     updateTime = LocalDateTime.now().format(pattern);
     //       System.out.println("syncOrder updateTime = " + updateTime);
   //     }

        if (direction == 1) {
            return orderService.syncEnterOrder(tenantID, enterStationLineCode, enterStationCode, updateTime);
        } else if(direction == 2) {
            return orderService.syncExitOrder(tenantID, exitStationLineCode, exitStationCode, updateTime);
        } else {
            return null;
        }

    }



}
