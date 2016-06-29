package utils.okhttp.request;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.okhttp.OkHttpUtils;
import utils.okhttp.callback.Callback;

/**
 * 对 OkHttpRequest 的封装，对外提供更多的接口：cancel(), readTimeOut() ...
 */
public class RequestCall {
    private OkHttpRequest okHttpRequest;
    private Request request;
    private Call call;

    private long readTimeOut;
    private long writeTimeOut;
    private long connTimeOut;

    public RequestCall(OkHttpRequest request) {
        this.okHttpRequest = request;
    }

    public RequestCall readTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return this;
    }

    public RequestCall writeTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    public RequestCall connTimeOut(long connTimeOut) {
        this.connTimeOut = connTimeOut;
        return this;
    }

    public Call buildCall(Callback callback) {
        request = generateRequest(callback);
        OkHttpClient okHttpClient = OkHttpUtils.getInstance().getOkHttpClient();
        if (readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0) {
            readTimeOut = readTimeOut > 0 ? readTimeOut : okHttpClient.readTimeoutMillis();
            writeTimeOut = writeTimeOut > 0 ? writeTimeOut : okHttpClient.writeTimeoutMillis();
            connTimeOut = connTimeOut > 0 ? connTimeOut : okHttpClient.connectTimeoutMillis();
            call = okHttpClient.newBuilder()
                    .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                    .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
                    .build()
                    .newCall(request);
        } else
            call = okHttpClient.newCall(request);
        return call;
    }

    private Request generateRequest(Callback callback) {
        return okHttpRequest.generateRequest(callback);
    }

    public void execute(Callback callback) {
        buildCall(callback);
        if (callback != null)
            callback.onBefore(request);
        OkHttpUtils.getInstance().execute(this, callback);
    }

    public Call getCall() {
        return call;
    }

    public synchronized Request getRequest() {
        if (null == request)
            request = generateRequest(null);
        return request;
    }

    public OkHttpRequest getOkHttpRequest() {
        return okHttpRequest;
    }

    public Response execute() throws IOException {
        buildCall(null);
        return call.execute();
    }

    public void cancel() {
        if (call != null)
            call.cancel();
    }
}
