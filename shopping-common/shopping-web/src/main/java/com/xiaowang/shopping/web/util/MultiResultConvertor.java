package com.xiaowang.shopping.web.util;


import com.xiaowang.shopping.base.response.PageResponse;
import com.xiaowang.shopping.web.vo.MultiResult;

import static com.xiaowang.shopping.base.response.ResponseCode.SUCCESS;

/**
 * @author cola
 */
public class MultiResultConvertor {

    public static <T> MultiResult<T> convert(PageResponse<T> pageResponse) {
        MultiResult<T> multiResult = new MultiResult<T>(true, SUCCESS.name(), SUCCESS.name(), pageResponse.getDatas(), pageResponse.getTotal(), pageResponse.getCurrentPage(), pageResponse.getPageSize());
        return multiResult;
    }
}
