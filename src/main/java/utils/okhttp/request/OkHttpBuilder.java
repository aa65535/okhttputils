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

    long connTimeOut;
    long writeTimeOut;
    long readTimeOut;

    public OkHttpBuilder() {
        headers = new Builder();
        this.callback = Callback.CALLBACK_DEFAULT;
    }

    OkHttpBuilder(OkHttpRequest request) {
        this.url = request.url;
        this.tag = request.tag;
        this.headers = request.headers;
        this.callback = request.callback;
        this.connTimeOut = request.connTimeOut;
        this.writeTimeOut = request.writeTimeOut;
        this.readTimeOut = request.readTimeOut;
    }

    /**
     * 根据当前实例创建一个 {@link OkHttpRequest} 对象
     */
    public abstract OkHttpRequest build();

    /**
     * 设置请求的 URL 地址
     */
    public T url(String url) {
        this.url = url;
        return (T) this;
    }

    /**
     * 设置请求的 TAG
     */
    public T tag(Object tag) {
        this.tag = tag;
        return (T) this;
    }

    /**
     * 设置求请的回调
     */
    public T callback(Callback callback) {
        if (callback != null)
            this.callback = callback;
        return (T) this;
    }

    /**
     * 设置请求头
     */
    public T headers(Headers headers) {
        this.headers = headers.newBuilder();
        return (T) this;
    }

    /**
     * 设置请求头
     */
    public T headers(Map<String, String> headers) {
        removeAllHeaders();
        addHeaders(headers);
        return (T) this;
    }

    /**
     * 添加请求头
     */
    public T addHeaders(Map<String, String> headers) {
        if (null != headers) {
            for (Entry<String, String> entry : headers.entrySet()) {
                this.headers.add(entry.getKey(), entry.getValue());
            }
        }
        return (T) this;
    }

    /**
     * 设置指定 {@code name} 的请求头的值
     */
    public T setHeader(String name, Object value) {
        if (null == value)
            throw new NullPointerException("the header [" + name + "] can not be null.");
        headers.set(name, value.toString());
        return (T) this;
    }

    /**
     * 添加一个请求头
     */
    public T addHeader(String name, Object value) {
        if (null == value)
            throw new NullPointerException("the header [" + name + "] can not be null.");
        putHeader(name, value);
        return (T) this;
    }

    /**
     * 添加一个请求头，如果 {@code value} 为空，则不添加
     */
    public T addOptionHeader(String name, Object value) {
        if (!Util.isEmpty(value))
            putHeader(name, value);
        return (T) this;
    }

    /**
     * 添加一个请求头，{@code value} 不可为空
     */
    public T addNonEmptyHeader(String name, Object value) {
        if (Util.isEmpty(value))
            throw new IllegalArgumentException("the header [" + name + "] can not be empty.");
        putHeader(name, value);
        return (T) this;
    }

    /**
     * 添加一个请求头，{@code value} 为空时添加空字符串
     */
    public T addNullableHeader(String name, Object value) {
        if (null != value)
            putHeader(name, value);
        else
            putHeader(name, "");
        return (T) this;
    }

    /**
     * 根据给定的 {@code name}，删除一个请求头
     */
    public T removeHeader(String name) {
        headers.removeAll(name);
        return (T) this;
    }

    /**
     * 删除所有请求头
     */
    public T removeAllHeaders() {
        for (String name : headers.build().names())
            headers.removeAll(name);
        return (T) this;
    }

    protected void putHeader(String name, Object value) {
        headers.add(name, value.toString());
    }

    /**
     * 设置本次请求的连接超时，单位毫秒
     */
    public T connTimeOut(long connTimeOut) {
        this.connTimeOut = connTimeOut;
        return (T) this;
    }

    /**
     * 设置本次请求的写入超时，单位毫秒
     */
    public T writeTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return (T) this;
    }

    /**
     * 设置本次请求的读取超时，单位毫秒
     */
    public T readTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return (T) this;
    }
}
