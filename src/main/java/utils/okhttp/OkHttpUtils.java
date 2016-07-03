package utils.okhttp;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import utils.okhttp.callback.Callback;
import utils.okhttp.cookie.CookieJarImpl;
import utils.okhttp.cookie.store.CookieStore;
import utils.okhttp.cookie.store.HasCookieStore;
import utils.okhttp.cookie.store.MemoryCookieStore;
import utils.okhttp.request.GetBuilder;
import utils.okhttp.request.HeadBuilder;
import utils.okhttp.request.OtherBuilder;
import utils.okhttp.request.PostFileBuilder;
import utils.okhttp.request.PostFormBuilder;
import utils.okhttp.request.PostStringBuilder;
import utils.okhttp.utils.Method;
import utils.okhttp.utils.ThreadExecutor;

@SuppressWarnings("unused")
public class OkHttpUtils {
    private OkHttpClient mOkHttpClient;
    private final ThreadExecutor mThreadExecutor;
    private volatile static OkHttpUtils mInstance;

    private OkHttpUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null)
            mOkHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new CookieJarImpl(new MemoryCookieStore()))
                    .build();
        else
            mOkHttpClient = okHttpClient;

        mThreadExecutor = new ThreadExecutor();
    }

    public synchronized static OkHttpUtils initClient(OkHttpClient okHttpClient) {
        if (null != mInstance)
            throw new IllegalStateException("Instance already exist, it can not be created again.");
        mInstance = new OkHttpUtils(okHttpClient);
        return mInstance;
    }

    public synchronized static OkHttpUtils getInstance() {
        if (mInstance == null)
            mInstance = new OkHttpUtils(null);
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

    public static OtherBuilder put() {
        return new OtherBuilder(Method.PUT);
    }

    public static OtherBuilder delete() {
        return new OtherBuilder(Method.DELETE);
    }

    public static OtherBuilder patch() {
        return new OtherBuilder(Method.PATCH);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public synchronized ThreadExecutor getThreadExecutor() {
        return mThreadExecutor;
    }

    public CookieStore getCookieStore() {
        final CookieJar cookieJar = mOkHttpClient.cookieJar();
        if (cookieJar == null)
            throw new IllegalStateException("you should invoked okHttpClientBuilder.cookieJar() to set a cookieJar.");
        if (cookieJar instanceof HasCookieStore)
            return ((HasCookieStore) cookieJar).getCookieStore();
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
        for (Call call : mOkHttpClient.dispatcher().queuedCalls())
            if (tag.equals(call.request().tag()))
                call.cancel();

        for (Call call : mOkHttpClient.dispatcher().runningCalls())
            if (tag.equals(call.request().tag()))
                call.cancel();
    }

    public void execute(Call call, final Callback callback) {
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                sendFailResultCallback(call, e, callback);
            }

            @Override
            public void onResponse(final Call call, final Response response) {
                try {
                    if (call.isCanceled()) {
                        sendFailResultCallback(call, new IOException("Canceled!"), callback);
                        return;
                    }
                    if (!response.isSuccessful()) {
                        sendFailResultCallback(call, new RuntimeException(response.body().string()), callback);
                        return;
                    }
                    sendSuccessResultCallback(callback.parseNetworkResponse(response), callback);
                } catch (Exception e) {
                    sendFailResultCallback(call, e, callback);
                } finally {
                    if (response.body() != null)
                        response.body().close();
                }
            }
        });
    }
}
