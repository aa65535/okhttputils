package utils.okhttp.request;

import java.util.LinkedHashMap;
import java.util.Map;

import utils.okhttp.utils.Util;

/**
 * 携带请求参数的 Builder
 * 对于 POST 请求是添加到请求体，对于 GET 请求则添加到 URL 的 query 中
 */
@SuppressWarnings("unchecked")
public abstract class ParamsBuilder<T extends ParamsBuilder> extends OkHttpBuilder<T> {
    Map<String, String> params;

    public ParamsBuilder() {
        params = new LinkedHashMap<>();
    }

    ParamsBuilder(OkHttpRequest request) {
        super(request);
    }

    /**
     * 设置请求参数
     */
    public T params(Map<String, String> params) {
        this.params = params;
        return (T) this;
    }

    /**
     * 添加请求参数
     */
    public T addParams(Map<String, String> params) {
        if (null != params) {
            this.params.putAll(params);
        }
        return (T) this;
    }

    /**
     * 添加请求参数， {@code value} 不可为 {@code null}
     */
    public T addParam(String name, Object value) {
        if (null == value)
            throw new NullPointerException("the params [" + name + "] can not be null.");
        putParam(name, value);
        return (T) this;
    }

    /**
     * 添加请求参数， {@code value} 为空时不添加
     */
    public T addOptionParam(String name, Object value) {
        if (!Util.isEmpty(value))
            putParam(name, value);
        return (T) this;
    }

    /**
     * 添加请求参数， {@code value} 不可为空
     */
    public T addNonEmptyParam(String name, Object value) {
        if (Util.isEmpty(value))
            throw new IllegalArgumentException("the params [" + name + "] can not be empty.");
        putParam(name, value);
        return (T) this;
    }

    /**
     * 添加请求参数， {@code value} 为 {@code null} 时添加空字符串
     */
    public T addNullableParam(String name, Object value) {
        if (null != value)
            putParam(name, value);
        else
            putParam(name, "");
        return (T) this;
    }

    /**
     * 移除给定 {@code name} 的请求参数
     */
    public T removeParam(String name) {
        params.remove(name);
        return (T) this;
    }

    /**
     * 移除所有请求参数
     */
    public T removeAllParam() {
        params.clear();
        return (T) this;
    }

    protected void putParam(String name, Object value) {
        params.put(name, value.toString());
    }
}
