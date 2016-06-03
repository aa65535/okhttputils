package utils.okhttp;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.CookieJar;
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
import utils.okhttp.request.RequestCall;
import utils.okhttp.utils.ThreadExecutor;

@SuppressWarnings("unused")
public class OkHttpUtils {
    public static final long DEFAULT_MILLISECONDS = 10_000;
    private volatile OkHttpClient mOkHttpClient;
    private volatile ThreadExecutor mThreadExecutor;
    private volatile static OkHttpUtils mInstance;

    private OkHttpUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new CookieJarImpl(new MemoryCookieStore()))
                    .build();
        } else {
            mOkHttpClient = okHttpClient;
        }
        mThreadExecutor = new ThreadExecutor();
    }

    public synchronized static OkHttpUtils initClient(OkHttpClient okHttpClient) {
        if (null != mInstance) {
            throw new UnsupportedOperationException("This method should be called before getInstance().");
        }
        mInstance = new OkHttpUtils(okHttpClient);
        return mInstance;
    }

    public synchronized static OkHttpUtils getInstance() {
        if (mInstance == null) {
            mInstance = new OkHttpUtils(null);
        }
        return mInstance;
    }

    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    public static HeadBuilder head() {
        return new HeadBuilder();
    }

    public static OtherRequestBuilder put() {
        return new OtherRequestBuilder(METHOD.PUT);
    }

    public static OtherRequestBuilder delete() {
        return new OtherRequestBuilder(METHOD.DELETE);
    }

    public static OtherRequestBuilder patch() {
        return new OtherRequestBuilder(METHOD.PATCH);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public synchronized ThreadExecutor getThreadExecutor() {
        return mThreadExecutor;
    }

    public CookieStore getCookieStore() {
        final CookieJar cookieJar = mOkHttpClient.cookieJar();
        if (cookieJar == null) {
            throw new IllegalArgumentException("you should invoked okHttpClientBuilder.cookieJar() to set a cookieJar.");
        }
        if (cookieJar instanceof HasCookieStore) {
            return ((HasCookieStore) cookieJar).getCookieStore();
        }
        return null;
    }

    public void sendFailResultCallback(final Call call, final Exception e, final Callback callback) {
        if (callback == null)
            return;
        getThreadExecutor().execute(new Runnable() {
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
        getThreadExecutor().execute(new Runnable() {
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
