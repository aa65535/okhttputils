package utils.okhttp.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadExecutor {
    private final static Executor EXECUTOR = Executors.newCachedThreadPool();

    protected Executor getThreadExecutor() {
        return EXECUTOR;
    }

    public void execute(Runnable runnable) {
        getThreadExecutor().execute(runnable);
    }
}
