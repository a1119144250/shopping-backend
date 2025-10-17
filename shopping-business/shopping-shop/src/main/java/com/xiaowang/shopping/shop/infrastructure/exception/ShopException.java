package com.xiaowang.shopping.shop.infrastructure.exception;

import com.xiaowang.shopping.base.exception.BizException;
import com.xiaowang.shopping.base.exception.ErrorCode;

/**
 * 商城模块异常
 * 
 * @author xiaowang
 */
public class ShopException extends BizException {

    public ShopException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ShopException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ShopException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public ShopException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }
}

