package org.apache.iotdb.admin.model.dto;


import static org.apache.iotdb.admin.common.constant.ErrorConst.SYSTEM_CODE;

/**
 * 错位状态码配置类，规范8位规范状态码
 *
 * @author 辰行
 * @date 2020/10/15
 */
public class ErrorInfoDTO extends CodeMsgDTO {

    public ErrorInfoDTO(long errorCode, String message) {
        super(SYSTEM_CODE * 10000 + errorCode, message);
    }
}
