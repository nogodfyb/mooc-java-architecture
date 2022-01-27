package com.example.demo1.common;

import lombok.Data;

@Data
public class CommonResult<T> {
    //前端提示使用
    private String msg;
    //状态码
    private int status;
    //前端使用数据
    private T data;

    public CommonResult(int code, String message, T data) {
        this.msg = message;
        this.status = code;
        this.data = data;
    }

    public CommonResult() {
    }

    /**
     * 操作成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 操作成功返回结果
     */
    public static <T> CommonResult<T> success() {
        return new CommonResult<T>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), null);
    }

    public static <T> CommonResult<T> success(String msg) {
        return new CommonResult<T>(ResultCodeEnum.SUCCESS.getCode(), msg, null);
    }

    /**
     * 操作失败返回结果
     */
    public static <T> CommonResult<T> failed() {
        return new CommonResult<T>(ResultCodeEnum.FAILED.getCode(), ResultCodeEnum.FAILED.getMessage(), null);
    }

    /**
     * 操作失败返回结果
     */
    public static <T> CommonResult<T> failed(String msg) {
        return new CommonResult<T>(ResultCodeEnum.FAILED.getCode(), msg, null);
    }

    /**
     * 参数校验失败返回结果
     */
    public static <T> CommonResult<T> validateFailed() {
        return new CommonResult<T>(ResultCodeEnum.VALIDATE_FAILED.getCode(),
                ResultCodeEnum.VALIDATE_FAILED.getMessage(), null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> CommonResult<T> unauthorized() {
        return new CommonResult<T>(ResultCodeEnum.UNAUTHORIZED.getCode(), ResultCodeEnum.UNAUTHORIZED.getMessage(), null);
    }

    /**
     * 未授权返回结果
     */
    public static <T> CommonResult<T> forbidden() {
        return new CommonResult<T>(ResultCodeEnum.FORBIDDEN.getCode(), ResultCodeEnum.FORBIDDEN.getMessage(), null);
    }

    /**
     * 版本过期返回结果
     */

    public static CommonResult versionExpired() {

        return new CommonResult(ResultCodeEnum.VERSION_EXPIRED.getCode(), ResultCodeEnum.VERSION_EXPIRED.getMessage(), null);

    }


}
