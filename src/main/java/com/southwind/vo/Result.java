package com.southwind.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果类
 */
@Data
public class Result<T> {
    // 状态码
    private Integer code;
    // 消息
    private String message;
    // 数据
    private T data;
    // 附加数据
    private Map<String, Object> extra = new HashMap<>();

    public Result() {
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功");
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 提示信息
     */
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(200, message, data);
    }

    /**
     * 失败返回结果
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败");
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message);
    }

    /**
     * 失败返回结果
     *
     * @param code    状态码
     * @param message 提示信息
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }



    /**
     * 添加额外数据
     *
     * @param key   键
     * @param value 值
     */
    public Result<T> put(String key, Object value) {
        this.extra.put(key, value);
        return this;
    }
}
