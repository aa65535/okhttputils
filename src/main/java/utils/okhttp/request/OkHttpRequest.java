package utils.okhttp.request;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import utils.okhttp.OkHttpUtils;
import utils.okhttp.callback.Callback;

@SuppressWarnings("unchecked")
public abstract class OkHttpRequest {
    String url;
    Object tag;
    Builder headers;
    Callback callback;

    long connTimeOut;
    long writeTimeOut;
    long readTimeOut;

    protected Call call;
    protected Request.Builder builder;

    OkHttpRequest(OkHttpBuilder builder) {
        this.url = builder.url;
        this.tag = builder.tag;
        this.headers = builder.headers;
        this.callback = builder.callback;
        this.connTimeOut = builder.connTimeOut;
        this.writeTimeOut = builder.writeTimeOut;
        this.readTimeOut = builder.readTimeOut;
        this.call = buildCall();
        this.builder = new Request.Builder().url(url).tag(tag).headers(headers.build());
    }

    /**
     * 根据当前实例创建一个 {@link OkHttpBuilder} 对象
     */
    public abstract OkHttpBuilder newBuilder();

    protected abstract RequestBody buildRequestBody();

    protected abstract Request buildRequest(RequestBody requestBody);

    protected RequestBody wrapRequestBody(RequestBody requestBody) {
        return requestBody;
    }

    protected Request generateRequest() {
        RequestBody requestBody = buildRequestBody();
        RequestBody wrappedRequestBody = wrapRequestBody(requestBody);
        return buildRequest(wrappedRequestBody);
    }

    protected Call buildCall() {
        OkHttpClient okHttpClient = OkHttpUtils.getInstance().getOkHttpClient();
        if (connTimeOut > 0 || writeTimeOut > 0 || readTimeOut > 0) {
            connTimeOut = connTimeOut > 0 ? connTimeOut : okHttpClient.connectTimeoutMillis();
            writeTimeOut = writeTimeOut > 0 ? writeTimeOut : okHttpClient.writeTimeoutMillis();
            readTimeOut = readTimeOut > 0 ? readTimeOut : okHttpClient.readTimeoutMillis();
            return okHttpClient.newBuilder()
                    .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                    .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                    .build()
                    .newCall(generateRequest());
        } else
            return okHttpClient.newCall(generateRequest());
    }

    /**
     * 返回当前实例的 {@link #call} 对象
     */
    public Call call() {
        return call;
    }

    /**
     * 返回当前实例的 {@link #callback} 对象
     */
    public Callback callback() {
        return callback;
    }

    /**
     * 返回当前实例的 {@link Request} 对象
     */
    public Request request() {
        return call.request();
    }

    /**
     * 执行同步请求，并返回 {@link Response} 对象
     *
     * @throws IOException
     */
    public Response response() throws IOException {
        return call.execute();
    }

    /**
     * 执行异步请求，期间会调用 {@link #callback} 的相关方法
     */
    public void execute() {
        if (call.isExecuted())
            throw new IllegalStateException("Already Executed");
        callback.onBefore(request());
        OkHttpUtils.getInstance().execute(call, callback);
    }

    /**
     * 取消本次请求
     */
    public void cancel() {
        call.cancel();
    }
}
