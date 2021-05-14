package org.apache.iotdb.admin.common.constant;


import org.apache.iotdb.admin.model.vo.HttpCodeVO;

import java.util.HashMap;
import java.util.Map;

/**
 * @anthor fyx 2021/5/13
 */
public final class HttpCodeConstant {
    public static final HttpCodeVO HTTP_CODE_FAIL;
    private static final Map<Long, HttpCodeVO> HTTP_CODE_MAP;

    private HttpCodeConstant() {
    }

    public static HttpCodeVO getHttpCode(long errorCode) {
        HttpCodeVO httpCodeVO = (HttpCodeVO)HTTP_CODE_MAP.get(errorCode);
        return httpCodeVO != null ? httpCodeVO : HTTP_CODE_FAIL;
    }

    public static void putHttpCode(long errorCode, long code, String message) {
        HttpCodeVO httpCodeVO = new HttpCodeVO(code, message);
        HTTP_CODE_MAP.put(errorCode, httpCodeVO);
    }

    static {
        HTTP_CODE_FAIL = new HttpCodeVO(CommonConstant.CODE_FAIL, "system error");
        HTTP_CODE_MAP = new HashMap();
    }
}
