package utils.okhttp;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;

import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import utils.okhttp.builder.GetBuilder;
import utils.okhttp.builder.HeadBuilder;
import utils.okhttp.builder.OtherRequestBuilder;
import utils.okhttp.builder.PostFileBuilder;
import utils.okhttp.builder.PostFormBuilder;
import utils.okhttp.builder.PostStringBuilder;
import utils.okhttp.callback.Callback;
import utils.okhttp.cookie.CookieJarImpl;
import utils.okhttp.cookie.store.CookieStore;
import utils.okhttp.cookie.store.HasCookieStore;
import utils.okhttp.cookie.store.MemoryCookieStore;
import utils.okhttp.https.HttpsUtils;
import utils.okhttp.https.HttpsUtils.UnSafeHostnameVerifier;
import utils.okhttp.request.RequestCall;
import utils.okhttp.utils.ThreadExecutor;

@SuppressWarnings("unused")
public class OkHttpUtils {
    public static final long DEFAULT_MILLISECONDS = 10_000;
    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private ThreadExecutor mThreadExecutor;

    public OkHttpUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
            //cookie enabled
            okHttpClientBuilder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
            okHttpClientBuilder.hostnameVerifier(new UnSafeHostnameVerifier());
            mOkHttpClient = okHttpClientBuilder.build();
        } else {
            mOkHttpClient = okHttpClient;
        }
        mThreadExecutor = new ThreadExecutor();
    }

    public static OkHttpUtils getInstance(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils(okHttpClient);
                }
            }
        }
        return mInstance;
    }

    public static OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils(null);
                }
            }
        }
        return mInstance;
    }

    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    public static OtherRequestBuilder put() {
        return new OtherRequestBuilder(METHOD.PUT);
    }

    public static HeadBuilder head() {
        return new HeadBuilder();
    }

    public static OtherRequestBuilder delete() {
        return new OtherRequestBuilder(METHOD.DELETE);
    }

    public static OtherRequestBuilder patch() {
        return new OtherRequestBuilder(METHOD.PATCH);
    }

    public OkHttpUtils addInterceptor(Interceptor interceptor) {
        mOkHttpClient = getOkHttpClient().newBuilder().addInterceptor(interceptor).build();
        return this;
    }

    /**
     * for https-way authentication
     */
    public OkHttpUtils setCertificates(InputStream... certificates) {
        setCertificates(certificates, null, null);
        return this;
    }

    /**
     * for https mutual authentication
     */
    public OkHttpUtils setCertificates(InputStream[] certificates, InputStream bksFile, String password) {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, bksFile, password))
                .build();
        return this;
    }

    public OkHttpUtils setHostNameVerifier(HostnameVerifier hostNameVerifier) {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .hostnameVerifier(hostNameVerifier)
                .build();
        return this;
    }

    public OkHttpUtils setTimeout(int timeout, TimeUnit units) {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .connectTimeout(timeout, units)
                .readTimeout(timeout, units)
                .writeTimeout(timeout, units)
                .build();
        return this;
    }

    public OkHttpUtils setReadTimeout(int timeout, TimeUnit units) {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .readTimeout(timeout, units)
                .build();
        return this;
    }

    public OkHttpUtils setWriteTimeout(int timeout, TimeUnit units) {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .writeTimeout(timeout, units)
                .build();
        return this;
    }

    public ThreadExecutor getThreadExecutor() {
        return mThreadExecutor;
    }

    public OkHttpUtils setThreadExecutor(ThreadExecutor threadExecutor) {
        mThreadExecutor = threadExecutor;
        return this;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public CookieStore getCookieStore() {
        final CookieJar cookieJar = mOkHttpClient.cookieJar();
        if (cookieJar == null) {
            throw new IllegalArgumentException("you should invoked okHttpClientBuilder.cookieJar() to set a cookieJar.");
        }
        if (cookieJar instanceof HasCookieStore) {
            return ((HasCookieStore) cookieJar).getCookieStore();
        } else {
            return null;
        }
    }

    public void sendFailResultCallback(final Call call, final Exception e, final Callback callback) {
        if (callback == null)
            return;
        mThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onError(call, e);
                callback.onAfter();
            }
        });
    }

    public <T> void sendSuccessResultCallback(final T object, final Callback<T> callback) {
        if (callback == null)
            return;
        mThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.onResponse(object);
                    callback.onAfter();
                } catch (Exception e) {
                    sendFailResultCallback(null, e, callback);
                }
            }
        });
    }

    public void cancelTag(Object tag) {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    public void execute(final RequestCall requestCall, Callback<?> callback) {
        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT;
        final Callback finalCallback = callback;

        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                sendFailResultCallback(call, e, finalCallback);
            }

            @Override
            public void onResponse(final Call call, final Response response) {
                if (call.isCanceled()) {
                    sendFailResultCallback(call, new IOException("Canceled!"), finalCallback);
                    return;
                }
                if (!response.isSuccessful()) {
                    try {
                        sendFailResultCallback(call, new RuntimeException(response.body().string()), finalCallback);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                try {
                    sendSuccessResultCallback(finalCallback.parseNetworkResponse(response), finalCallback);
                } catch (Exception e) {
                    sendFailResultCallback(call, e, finalCallback);
                }
            }
        });
    }

    public interface METHOD {
        String HEAD = "HEAD";
        String DELETE = "DELETE";
        String PUT = "PUT";
        String PATCH = "PATCH";
    }
}
