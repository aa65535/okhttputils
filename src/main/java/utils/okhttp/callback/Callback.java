package utils.okhttp.callback;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public abstract class Callback<T> {
    public static final Callback CALLBACK_DEFAULT = new Callback() {
        @Override
        public Object parseNetworkResponse(Response response) throws Exception {
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
     * UI Thread
     */
    public void onBefore(Request request) {
    }

    /**
     * UI Thread
     */
    public void onAfter() {
    }

    /**
     * UI Thread
     */
    public void inProgress(long progress, long total) {
    }

    /**
     * Thread Pool Thread
     */
    public abstract T parseNetworkResponse(Response response) throws Exception;

    public abstract void onError(Call call, Exception e);

    public abstract void onResponse(T t);
}
