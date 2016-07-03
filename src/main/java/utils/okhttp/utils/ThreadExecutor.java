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

    /**
     * 获取一个线程池，会尝试创建 Android 下的 UI 池线程
     * 如果失败则创建一个普通的 Java 线程池
     */
    private static Executor findExecutors() {
        try {
            return new AndroidThreadExecutor();
        } catch (Exception ignored) {
        }
        return Executors.newCachedThreadPool();
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    /**
     * Android 下的 UI 池线程
     */
    static class AndroidThreadExecutor implements Executor {
        private final Object handler;
        private final Method postMethod;

        public AndroidThreadExecutor() throws Exception {
            Class<?> looperClass = Class.forName("android.os.Looper");
            Class<?> handlerClass = Class.forName("android.os.Handler");
            Method getMainLooperMethod = looperClass.getDeclaredMethod("getMainLooper");
            Object looper = getMainLooperMethod.invoke(null);
            Constructor<?> handlerConstructor = handlerClass.getConstructor(looperClass);
            handler = handlerConstructor.newInstance(looper);
            postMethod = handlerClass.getDeclaredMethod("post", Runnable.class);
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
