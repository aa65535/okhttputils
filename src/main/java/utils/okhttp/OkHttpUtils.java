package utils.okhttp;

import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import utils.okhttp.cookie.store.CookieStore;
import utils.okhttp.cookie.store.HasCookieStore;
import utils.okhttp.request.GetBuilder;
import utils.okhttp.request.HeadBuilder;
import utils.okhttp.request.OtherBuilder;
import utils.okhttp.request.PostFileBuilder;
import utils.okhttp.request.PostFormBuilder;
import utils.okhttp.request.PostStringBuilder;
import utils.okhttp.utils.Method;
import utils.okhttp.utils.ThreadExecutor;
import utils.okhttp.utils.Objects;

@SuppressWarnings("unused")
public class OkHttpUtils {
    private final OkHttpClient mOkHttpClient;
    private final ThreadExecutor mThreadExecutor;
    private volatile static OkHttpUtils mInstance;

    private OkHttpUtils(OkHttpClient okHttpClient) {
        mOkHttpClient = Objects.getDefinedObject(okHttpClient, new OkHttpClient.Builder().build());
        mThreadExecutor = new ThreadExecutor();
    }

    /**
     * 初始化 {@link OkHttpClient} 对象，需要在 {@link #getInstance} 调用之前执行
     */
    public synchronized static OkHttpUtils initClient(OkHttpClient okHttpClient) {
        if (Objects.nonNull(mInstance))
            throw new IllegalStateException("Instance already exist, it can not be created again.");
        return (mInstance = new OkHttpUtils(okHttpClient));
    }

    /**
     * 获取一个 OkHttpUtils 实例
     */
    public synchronized static OkHttpUtils getInstance() {
        if (Objects.nonNull(mInstance))
            return mInstance;
        return (mInstance = new OkHttpUtils(null));
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
        if (Objects.isNull(cookieJar))
            throw new IllegalStateException("you should invoked okHttpClientBuilder.cookieJar() to set a cookieJar.");
        if (cookieJar instanceof HasCookieStore)
            return ((HasCookieStore) cookieJar).getCookieStore();
        return null;
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
}
