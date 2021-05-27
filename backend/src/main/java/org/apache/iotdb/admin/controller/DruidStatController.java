//package org.apache.iotdb.admin.controller;
//
//import org.apache.iotdb.admin.model.vo.BaseVO;
//import com.alibaba.druid.stat.DruidStatManagerFacade;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * ${DESCRIPTION}
// *
// * @author fanli
// */
//@RestController
//public class DruidStatController {
//
//    @GetMapping("/druid/stat")
//    public BaseVO<Object> druidStat() {
//        // DruidStatManagerFacade#getDataSourceStatDataList 该方法可以获取所有数据源的监控数据，除此之外 DruidStatManagerFacade 还提供了一些其他方法，你可以按需选择使用。
//        return BaseVO.success(DruidStatManagerFacade.getInstance().getDataSourceStatDataList());
//    }
//}
