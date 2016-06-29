package utils.okhttp.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import utils.okhttp.request.RequestCall;
import utils.okhttp.utils.Util;

@SuppressWarnings("unchecked")
public abstract class OkHttpRequestBuilder<T extends OkHttpRequestBuilder> {
    protected String url;
    protected Object tag;
    protected volatile Map<String, String> headers;

    public abstract RequestCall build();

    public T url(String url) {
        this.url = url;
        return (T) this;
    }

    public T tag(Object tag) {
        this.tag = tag;
        return (T) this;
    }

    public T headers(Map<String, String> headers) {
        this.headers = headers;
        return (T) this;
    }

    public T addHeaders(Map<String, String> headers) {
        if (null != headers) {
            if (this.headers == null)
                this.headers = headers;
            else
                this.headers.putAll(headers);
        }
        return (T) this;
    }

    public T addHeader(String key, Object value) {
        if (null == value)
            throw new NullPointerException("the params [" + key + "] value is null.");
        putHeader(key, value);
        return (T) this;
    }

    public T addOptionHeader(String key, Object value) {
        if (!Util.isEmpty(value))
            putHeader(key, value);
        return (T) this;
    }

    public T addNonEmptyHeader(String key, Object value) {
        if (Util.isEmpty(value))
            throw new NullPointerException("the params [" + key + "] value is empty.");
        putHeader(key, value);
        return (T) this;
    }

    public T addNullableHeader(String key, Object value) {
        if (null != value)
            putHeader(key, value);
        else
            putHeader(key, "");
        return (T) this;
    }

    protected synchronized void putHeader(String key, Object value) {
        if (headers == null)
            headers = new LinkedHashMap<>();
        headers.put(key, value.toString());
    }
}
