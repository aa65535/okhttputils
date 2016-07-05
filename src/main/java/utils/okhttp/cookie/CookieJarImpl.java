package utils.okhttp.cookie;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import utils.okhttp.cookie.store.CookieStore;
import utils.okhttp.cookie.store.HasCookieStore;
import utils.okhttp.utils.Objects;

/**
 * 对 {@link CookieJar} 的包装
 */
public class CookieJarImpl implements CookieJar, HasCookieStore {
    private CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        this.cookieStore = Objects.requireNonNull(cookieStore, "cookieStore can not be null.");
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.add(url, cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        return cookieStore.get(url);
    }

    @Override
    public CookieStore getCookieStore() {
        return cookieStore;
    }
}
