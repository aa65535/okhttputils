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
    private final OkHttpClient mOkHttpClient;
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

    /**
     * 初始化 {@link OkHttpClient} 对象，需要在 {@link #getInstance} 调用之前执行
     */
    public synchronized static OkHttpUtils initClient(OkHttpClient okHttpClient) {
        if (null != mInstance)
            throw new IllegalStateException("Instance already exist, it can not be created again.");
        mInstance = new OkHttpUtils(okHttpClient);
        return mInstance;
    }

    /**
     * 获取一个 OkHttpUtils 实例
     */
    public synchronized static OkHttpUtils getInstance() {
        if (mInstance == null)
            mInstance = new OkHttpUtils(null);
        return mInstance;
    }

    /**
     * 使用 GET 请求
     *
     * @return {@link GetBuilder} 对象
     */
    public static GetBuilder get() {
        return new GetBuilder();
    }

    /**
     * 使用 POST Form 请求
     *
     * @return {@link PostFormBuilder} 对象
     */
    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    /**
     * 使用 POST File 请求
     *
     * @return {@link PostFileBuilder} 对象
     */
    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    /**
     * 使用 POST String 请求
     *
     * @return {@link PostStringBuilder} 对象
     */
    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    /**
     * 使用 HEAD 请求
     *
     * @return {@link HeadBuilder} 对象
     */
    public static HeadBuilder head() {
        return new HeadBuilder();
    }

    /**
     * 使用 PUT 请求
     *
     * @return {@link OtherBuilder} 对象
     */
    public static OtherBuilder put() {
        return new OtherBuilder(Method.PUT);
    }

    /**
     * 使用 DELETE 请求
     *
     * @return {@link OtherBuilder} 对象
     */
    public static OtherBuilder delete() {
        return new OtherBuilder(Method.DELETE);
    }

    /**
     * 使用 PATCH 请求
     *
     * @return {@link OtherBuilder} 对象
     */
    public static OtherBuilder patch() {
        return new OtherBuilder(Method.PATCH);
    }

    /**
     * 获取已经初始化的 {@link OkHttpClient} 对象
     *
     * @return {@link OkHttpClient} 对象
     */
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 获取线程池对象
     *
     * @return {@link ThreadExecutor} 对象
     */
    public synchronized ThreadExecutor getThreadExecutor() {
        return mThreadExecutor;
    }

    /**
     * 获取 {@link OkHttpClient.Builder#cookieJar} 方法中传入的的 {@link CookieStore} 对象
     *
     * @return {@link CookieStore} 对象
     */
    public CookieStore getCookieStore() {
        final CookieJar cookieJar = mOkHttpClient.cookieJar();
        if (cookieJar == null)
            throw new IllegalStateException("you should invoked okHttpClientBuilder.cookieJar() to set a cookieJar.");
        if (cookieJar instanceof HasCookieStore)
            return ((HasCookieStore) cookieJar).getCookieStore();
        return null;
    }

    private void sendFailResultCallback(final Call call, final Exception e, final Callback callback) {
        mThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onError(call, e);
                callback.onAfter();
            }
        });
    }

    private <T> void sendSuccessResultCallback(final T object, final Callback<T> callback) {
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

    /**
     * 根据设置的 TAG 取消相应的网络请求
     */
    public void cancelTag(Object tag) {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls())
            if (tag.equals(call.request().tag()))
                call.cancel();

        for (Call call : mOkHttpClient.dispatcher().runningCalls())
            if (tag.equals(call.request().tag()))
                call.cancel();
    }

    /**
     * 执行网络请求
     *
     * @param call     {@link Call} 对象
     * @param callback {@link Callback} 对象
     */
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
