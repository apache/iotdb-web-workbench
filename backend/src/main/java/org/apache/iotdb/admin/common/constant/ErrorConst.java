package org.apache.iotdb.admin.common.constant;

import org.apache.iotdb.admin.model.dto.CodeMsgDTO;
import org.apache.iotdb.admin.model.dto.ErrorInfoDTO;

/**
 * @author 辰行
 * @date 2020/10/15
 */
public interface ErrorConst {

    /**
     * 前四位状态码表示具体项目
     */
    Integer SYSTEM_CODE = 1011;


    /**
     * 后四位状态码表示具体异常，根据自己的业务逻辑进行定义
     */
    CodeMsgDTO PARAM_ERROR = new ErrorInfoDTO(1000, "插入失败");
}
