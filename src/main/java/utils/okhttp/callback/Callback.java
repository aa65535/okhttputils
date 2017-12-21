package utils.okhttp.callback;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * {@link okhttp3.Callback} 的扩展类，包含请求之前到响应之后的回调方法
 *
 * @param <T> 指示 {@link #parseNetworkResponse} 方法返回的类型
 */
@SuppressWarnings("unused")
public abstract class Callback<T> {
    /**
     * 默认 Callback 实例，当异步请求没有传入 Callback 实例时调用
     */
    public static final Callback CALLBACK_DEFAULT = new Callback() {
        @Override
        public Object parseNetworkResponse(Response response) {
            return null;
        }

        @Override
        public void onError(Call call, Exception e) {
        }

        @Override
        public void onResponse(Object response) {
        }
    };

    /**
     * 网络请求发起之前调用，Android 下使用 UI 线程
     */
    public void onBefore(Request request) {
    }

    /**
     * 网络请求结束之后调用，Android 下使用 UI 线程
     */
    public void onAfter() {
    }

    /**
     * 网络请求过程中向服务器发送数据或从服务器接受数据的过程中调用，
     * 此方法并不一定总是被调用，Android 下使用 UI 线程
     *
     * @param progress 当前发送或者接受的字节数
     * @param total    总的字节数
     */
    public void inProgress(long progress, long total) {
    }

    /**
     * 解析网络请求响应数据的方法，由子类实现返回对应的对象，Android 下使用 Worker 线程
     *
     * @param response Response 实例，包含请求响应内容
     * @return 返回应的泛型类型
     * @throws Exception
     */
    public abstract T parseNetworkResponse(Response response) throws Exception;

    /**
     * 当网络请求被取消、连接问题、服务器返回错误码或超时执行时调用，Android 下使用 UI 线程
     */
    public abstract void onError(Call call, Exception e);

    /**
     * 当网络请求正确响应后执行，Android 下使用 UI 线程
     *
     * @param t {@link #parseNetworkResponse} 方法返回的对象
     */
    public abstract void onResponse(T t);
}
