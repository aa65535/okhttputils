package utils.okhttp.cookie.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class MemoryCookieStore implements CookieStore {
    private final HashMap<String, List<Cookie>> allCookies = new HashMap<>();

    @Override
    public void add(HttpUrl url, List<Cookie> cookies) {
        List<Cookie> oldCookies = get(url);

        for (Cookie cookie : cookies) {
            String va = cookie.name();
            Iterator<Cookie> itOld = oldCookies.iterator();
            while (va != null && itOld.hasNext())
                if (va.equals(itOld.next().name()))
                    itOld.remove();
        }
        oldCookies.addAll(cookies);
    }

    @Override
    public synchronized List<Cookie> get(HttpUrl url) {
        List<Cookie> cookies = allCookies.get(url.host());
        if (cookies == null) {
            cookies = new ArrayList<>();
            allCookies.put(url.host(), cookies);
        }
        return cookies;
    }

    @Override
    public List<Cookie> getCookies() {
        List<Cookie> cookies = new ArrayList<>();
        Set<String> urls = allCookies.keySet();
        for (String url : urls)
            cookies.addAll(allCookies.get(url));
        return cookies;
    }

    @Override
    public boolean remove(HttpUrl url, Cookie cookie) {
        List<Cookie> cookies = allCookies.get(url.host());
        return cookie != null && cookies.remove(cookie);
    }

    @Override
    public void removeAll() {
        allCookies.clear();
    }
}
