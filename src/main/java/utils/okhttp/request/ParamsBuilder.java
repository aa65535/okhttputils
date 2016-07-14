package utils.okhttp.request;

import java.util.LinkedHashMap;
import java.util.Map;

import utils.okhttp.utils.Objects;

/**
 * 携带请求参数的 Builder
 * 对于 POST 请求是添加到请求体，对于 GET 请求则添加到 URL 的 query 中
 */
@SuppressWarnings("unchecked")
public abstract class ParamsBuilder<T extends ParamsBuilder> extends OkHttpBuilder<T> {
    protected Map<String, String> params;

    public ParamsBuilder() {
        this.params = new LinkedHashMap<>();
    }

    protected ParamsBuilder(OkHttpRequest request) {
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
        if (Objects.nonNull(params)) {
            this.params.putAll(params);
        }
        return (T) this;
    }

    /**
     * 添加请求参数， {@code value} 不可为 {@code null}
     */
    public T addParam(String name, Object value) {
        putParam(name, Objects.requireNonNull(value, "params [" + name + "] is null."));
        return (T) this;
    }

    /**
     * 添加请求参数， {@code value} 为空时不添加
     */
    public T addOptionParam(String name, Object value) {
        if (!Objects.isEmpty(value))
            putParam(name, value);
        return (T) this;
    }

    /**
     * 添加请求参数， {@code value} 不可为空
     */
    public T addNonEmptyParam(String name, Object value) {
        putParam(name, Objects.requireNonEmpty(value, "params [" + name + "] is empty."));
        return (T) this;
    }

    /**
     * 添加请求参数， {@code value} 为 {@code null} 时添加空字符串
     */
    public T addNullableParam(String name, Object value) {
        if (Objects.nonNull(value))
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
