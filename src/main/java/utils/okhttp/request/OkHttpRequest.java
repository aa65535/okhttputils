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

    long readTimeOut;
    long writeTimeOut;
    long connTimeOut;

    protected Call call;
    protected Request.Builder builder;

    OkHttpRequest(OkHttpBuilder builder) {
        this.url = builder.url;
        this.tag = builder.tag;
        this.headers = builder.headers;
        this.callback = builder.callback;
        this.readTimeOut = builder.readTimeOut;
        this.writeTimeOut = builder.writeTimeOut;
        this.connTimeOut = builder.connTimeOut;
        this.call = buildCall();
        this.builder = new Request.Builder().url(url).tag(tag).headers(headers.build());
    }

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
        if (readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0) {
            readTimeOut = readTimeOut > 0 ? readTimeOut : okHttpClient.readTimeoutMillis();
            writeTimeOut = writeTimeOut > 0 ? writeTimeOut : okHttpClient.writeTimeoutMillis();
            connTimeOut = connTimeOut > 0 ? connTimeOut : okHttpClient.connectTimeoutMillis();
            return okHttpClient.newBuilder()
                    .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                    .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
                    .build()
                    .newCall(generateRequest());
        } else
            return okHttpClient.newCall(generateRequest());
    }

    public Call call() {
        return call;
    }

    public Callback callback() {
        return callback;
    }

    public Request request() {
        return call.request();
    }

    public Response response() throws IOException {
        return call.execute();
    }

    public void execute() {
        if (call.isExecuted())
            throw new IllegalStateException("Already Executed");
        callback.onBefore(request());
        OkHttpUtils.getInstance().execute(call, callback);
    }

    public void cancel() {
        call.cancel();
    }
}
