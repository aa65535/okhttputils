package utils.okhttp.cookie;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import utils.okhttp.cookie.store.CookieStore;
import utils.okhttp.cookie.store.HasCookieStore;
import utils.okhttp.utils.Objects;

/**
 * 对 {@link CookieJar} 的包装
 */
@SuppressWarnings("unused")
public class CookieJarImpl implements CookieJar, HasCookieStore {
    private final ReentrantLock lock;
    private final CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        this.lock = new ReentrantLock();
        this.cookieStore = Objects.requireNonNull(cookieStore, "cookieStore is null.");
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        lock.lock();
        try {
            for (Cookie cookie : cookies) {
                cookieStore.add(url, cookie);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        lock.lock();
        try {
            return cookieStore.get(url);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public CookieStore getCookieStore() {
        return cookieStore;
    }
}
