package utils.okhttp.request;

import java.util.LinkedHashMap;
import java.util.Map;

import utils.okhttp.utils.Util;

@SuppressWarnings("unchecked")
public abstract class ParamsBuilder<T extends ParamsBuilder> extends OkHttpBuilder<T> {
    Map<String, String> params;

    public ParamsBuilder() {
        params = new LinkedHashMap<>();
    }

    ParamsBuilder(OkHttpRequest request) {
        super(request);
    }

    public T params(Map<String, String> params) {
        this.params = params;
        return (T) this;
    }

    public T addParams(Map<String, String> params) {
        if (null != params) {
            this.params.putAll(params);
        }
        return (T) this;
    }

    public T addParam(String name, Object value) {
        if (null == value)
            throw new NullPointerException("the params [" + name + "] can not be null.");
        putParam(name, value);
        return (T) this;
    }

    public T addOptionParam(String name, Object value) {
        if (!Util.isEmpty(value))
            putParam(name, value);
        return (T) this;
    }

    public T addNonEmptyParam(String name, Object value) {
        if (Util.isEmpty(value))
            throw new IllegalArgumentException("the params [" + name + "] can not be empty.");
        putParam(name, value);
        return (T) this;
    }

    public T addNullableParam(String name, Object value) {
        if (null != value)
            putParam(name, value);
        else
            putParam(name, "");
        return (T) this;
    }

    public T removeParam(String name) {
        params.remove(name);
        return (T) this;
    }

    public T removeAllParam() {
        params.clear();
        return (T) this;
    }

    protected void putParam(String name, Object value) {
        params.put(name, value.toString());
    }
}
