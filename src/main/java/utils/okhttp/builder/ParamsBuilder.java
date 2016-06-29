package utils.okhttp.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import utils.okhttp.utils.Util;

/**
 * 带参数的 Builder
 * Created by Jian Chang on 2016-06-29.
 */
@SuppressWarnings("unchecked")
public abstract class ParamsBuilder<T extends OkHttpRequestBuilder> extends OkHttpRequestBuilder<T> {
    protected volatile Map<String, String> params;

    public T params(Map<String, String> params) {
        this.params = params;
        return (T) this;
    }

    public T addParams(Map<String, String> params) {
        if (null != params) {
            if (this.params == null)
                this.params = params;
            else
                this.params.putAll(params);
        }
        return (T) this;
    }

    public T addParam(String key, Object value) {
        if (null == value)
            throw new NullPointerException("the params [" + key + "] value is null.");
        putParam(key, value);
        return (T) this;
    }

    public T addOptionParam(String key, Object value) {
        if (!Util.isEmpty(value))
            putParam(key, value);
        return (T) this;
    }

    public T addNonEmptyParam(String key, Object value) {
        if (Util.isEmpty(value))
            throw new NullPointerException("the params [" + key + "] value is empty.");
        putParam(key, value);
        return (T) this;
    }

    public T addNullableParam(String key, Object value) {
        if (null != value)
            putParam(key, value);
        else
            putParam(key, "");
        return (T) this;
    }

    protected synchronized void putParam(String key, Object value) {
        if (params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, value.toString());
    }
}
