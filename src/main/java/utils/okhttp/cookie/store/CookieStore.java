package utils.okhttp.cookie.store;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public interface CookieStore {
    /**
     * 添加 {@code cookies}
     *
     * @param url     给定的 URL
     * @param cookies 待保存的 {@code cookies}
     */
    void add(HttpUrl url, List<Cookie> cookies);

    /**
     * 根据给定的 URL 获取 {@code cookies}
     *
     * @param url 给定的 URL
     */
    List<Cookie> get(HttpUrl url);

    /**
     * 获取全部 {@code cookies}
     */
    List<Cookie> getCookies();

    /**
     * 根据给定的 URL 删除 {@code cookies}
     *
     * @param url    给定的 URL
     * @param cookie 待删除的 {@code cookie}
     * @return {@code cookie} 存在并成功删除返回 {@code true} 否则返回 {@code false}
     */
    boolean remove(HttpUrl url, Cookie cookie);

    /**
     * 删除所有保存的 {@code cookies}
     */
    void removeAll();
}
