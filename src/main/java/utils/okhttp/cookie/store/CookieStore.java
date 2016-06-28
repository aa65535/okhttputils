package utils.okhttp.cookie.store;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public interface CookieStore {
    void add(HttpUrl url, List<Cookie> cookies);

    List<Cookie> get(HttpUrl url);

    List<Cookie> getCookies();

    boolean remove(HttpUrl url, Cookie cookie);

    void removeAll();
}
