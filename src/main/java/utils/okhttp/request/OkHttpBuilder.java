package utils.okhttp.request;

import java.util.Map;
import java.util.Map.Entry;

import okhttp3.Headers;
import okhttp3.Headers.Builder;
import utils.okhttp.callback.Callback;
import utils.okhttp.utils.Util;

@SuppressWarnings("unchecked")
public abstract class OkHttpBuilder<T extends OkHttpBuilder> {
    String url;
    Object tag;
    Builder headers;
    Callback callback;

    long readTimeOut;
    long writeTimeOut;
    long connTimeOut;

    public OkHttpBuilder() {
        headers = new Builder();
        this.callback = Callback.CALLBACK_DEFAULT;
    }

    OkHttpBuilder(OkHttpRequest request) {
        this.url = request.url;
        this.tag = request.tag;
        this.headers = request.headers;
        this.callback = request.callback;
    }

    public abstract OkHttpRequest build();

    public T url(String url) {
        this.url = url;
        return (T) this;
    }

    public T tag(Object tag) {
        this.tag = tag;
        return (T) this;
    }

    public T callback(Callback callback) {
        if (callback != null)
            this.callback = callback;
        return (T) this;
    }

    public T headers(Headers headers) {
        this.headers = headers.newBuilder();
        return (T) this;
    }

    public T headers(Map<String, String> headers) {
        removeAllHeader();
        addHeaders(headers);
        return (T) this;
    }

    public T addHeaders(Map<String, String> headers) {
        if (null != headers) {
            for (Entry<String, String> entry : headers.entrySet()) {
                this.headers.add(entry.getKey(), entry.getValue());
            }
        }
        return (T) this;
    }

    public T setHeader(String name, Object value) {
        if (null == value)
            throw new NullPointerException("the header [" + name + "] can not be null.");
        headers.set(name, value.toString());
        return (T) this;
    }

    public T addHeader(String name, Object value) {
        if (null == value)
            throw new NullPointerException("the header [" + name + "] can not be null.");
        putHeader(name, value);
        return (T) this;
    }

    public T addOptionHeader(String name, Object value) {
        if (!Util.isEmpty(value))
            putHeader(name, value);
        return (T) this;
    }

    public T addNonEmptyHeader(String name, Object value) {
        if (Util.isEmpty(value))
            throw new IllegalArgumentException("the header [" + name + "] can not be empty.");
        putHeader(name, value);
        return (T) this;
    }

    public T addNullableHeader(String name, Object value) {
        if (null != value)
            putHeader(name, value);
        else
            putHeader(name, "");
        return (T) this;
    }

    public T removeHeader(String name) {
        headers.removeAll(name);
        return (T) this;
    }

    public T removeAllHeader() {
        for (String name : headers.build().names())
            headers.removeAll(name);
        return (T) this;
    }

    protected void putHeader(String name, Object value) {
        headers.add(name, value.toString());
    }

    public T readTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return (T) this;
    }

    public T writeTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return (T) this;
    }

    public T connTimeOut(long connTimeOut) {
        this.connTimeOut = connTimeOut;
        return (T) this;
    }
}
