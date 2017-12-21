package utils.okhttp.cookie.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import utils.okhttp.utils.Objects;

@SuppressWarnings("unused")
public class MemoryCookieStore implements CookieStore {
    private final List<Cookie> cookieJar;

    public MemoryCookieStore() {
        cookieJar = new ArrayList<>();
    }

    @Override
    public void add(HttpUrl url, Cookie cookie) {
        Objects.requireNonNull(cookie, "cookie is null");
        remove(cookie);
        if (!isExpired(cookie)) {
            cookieJar.add(cookie);
        }
    }

    @Override
    public List<Cookie> get(HttpUrl url) {
        Objects.requireNonNull(url, "url is null");
        List<Cookie> cookies = new ArrayList<>();
        for (Cookie cookie : unexpired()) {
            if (cookie.matches(url)) {
                cookies.add(cookie);
            }
        }
        return Collections.unmodifiableList(cookies);
    }

    @Override
    public List<Cookie> getCookies() {
        return Collections.unmodifiableList(unexpired());
    }

    @Override
    public boolean remove(Cookie cookie) {
        Objects.requireNonNull(cookie, "cookie is null");
        Iterator<Cookie> it = cookieJar.iterator();
        while (it.hasNext()) {
            if (equals(it.next(), cookie)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeAll() {
        if (!cookieJar.isEmpty()) {
            cookieJar.clear();
            return true;
        }
        return false;
    }

    private List<Cookie> unexpired() {
        Iterator<Cookie> it = cookieJar.iterator();
        while (it.hasNext()) {
            if (isExpired(it.next())) {
                it.remove();
            }
        }
        return cookieJar;
    }

    private static boolean isExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    private static boolean equals(Cookie c1, Cookie c2) {
        return c1 == c2 ||
                Objects.equals(c1.name(), c2.name()) &&
                        Objects.equals(c1.domain(), c2.domain()) &&
                        Objects.equals(c1.path(), c2.path());
    }
}
