package com.h3c.ywrj.dzkf.hkcustoms.common;

import lombok.extern.slf4j.Slf4j;

/**
 * RestResult对象的创建工具类
 * <p>
 * Created by @author wfw2525 on 2019/12/10 17:22
 */
@Slf4j
public final class RestResultGenerator {
    public static <T> RestResult<T> genResult(int code, String message, T data) {
        return new RestResult<>(code, message, data);
    }

    public static <T> RestResult<T> genSuccessResult(String msg) {
        return genResult(0, msg, null);
    }

    public static <T> RestResult<T> genSuccessResult(T data) {
        return genResult(0, null, data);
    }

    public static <T> RestResult<T> genSuccessResult(String message, T data) {
        return genResult(0, message, data);
    }

    public static <T> RestResult<T> genErrorResult(int code, String message) {
        return genResult(code, message, null);
    }

    public static <T> RestResult<T> genErrorResult(int code, Throwable t) {
        return genErrorResult(code, "Exception: class (" + t.getClass() + "), clause (" + t.getMessage() + ")");
    }
}
