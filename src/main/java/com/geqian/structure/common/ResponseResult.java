package com.geqian.structure.common;

import java.io.Serializable;

/**
 * @author geqian
 * @date 15:19 2022/8/31
 */
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = 3769545126082051926L;

    private Integer code;
    private String message;
    private T data;

    public ResponseResult() {
    }

    public ResponseResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ResponseResult<T> success(String message) {
        return new ResponseResult<T>(200, message, null);
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<T>(200, "success", data);
    }

    public static <T> ResponseResult<T> success(String message, T data) {
        return new ResponseResult<T>(200, message, data);
    }

    public static <T> ResponseResult<T> success(Integer code, String message, T data) {
        return new ResponseResult<>(code, message, data);
    }

    public static <T> ResponseResult<T> fail(String message) {
        return new ResponseResult<T>(400, message, null);
    }

    public static <T> ResponseResult<T> fail(T data) {
        return new ResponseResult<T>(400, "fail", data);
    }


    public static <T> ResponseResult<T> fail(String message, T data) {
        return new ResponseResult<T>(400, message, data);
    }

    public static <T> ResponseResult<T> fail(Integer code, String message, T data) {
        return new ResponseResult<T>(code, message, data);
    }


    @Override
    public String toString() {
        return "ResponseResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
