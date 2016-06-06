package utils.okhttp.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadExecutor {
    private final Executor executor;

    public ThreadExecutor() {
        executor = findExecutors();
    }

    private static Executor findExecutors() {
        try {
            Class.forName("android.os.Handler");
            return new AndroidThreadExecutor();
        } catch (ClassNotFoundException ignored) {
        }
        return Executors.newCachedThreadPool();
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    static class AndroidThreadExecutor implements Executor {
        private final Object handler;
        private final Method postMethod;

        public AndroidThreadExecutor() {
            try {
                Class<?> looperClass = Class.forName("android.os.Looper");
                Class<?> handlerClass = Class.forName("android.os.Handler");
                Method getMainLooperMethod = looperClass.getDeclaredMethod("getMainLooper");
                Object looper = getMainLooperMethod.invoke(null);
                Constructor<?> handlerConstructor = handlerClass.getConstructor(looperClass);
                handler = handlerConstructor.newInstance(looper);
                postMethod = handlerClass.getDeclaredMethod("post", Runnable.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void execute(Runnable command) {
            try {
                postMethod.invoke(handler, command);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
