package utils.okhttp.cookie.store;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

@SuppressWarnings("unused")
public interface CookieStore {
    /**
     * 将一个 cookie 添加到存储区中。为每个传入的 HTTP 响应调用此方法。
     * <p/>
     * 如果对应于给定的 cookie 已经存在，则使用新的 cookie 替换它。
     *
     * @param url    与此 cookie 关联的 url
     * @param cookie 要存储的 cookie
     * @throws NullPointerException 如果 {@code cookie} 为 {@code null}
     * @see #get
     */
    void add(HttpUrl url, Cookie cookie);

    /**
     * 获取与给定 HttpUrl 匹配的 cookie。
     * 只返回未过期的 cookie。为每个传出的 HTTP 请求调用此方法。
     *
     * @param url 与要返回的 cookie 相匹配的 HttpUrl
     * @return Cookies 的不可变列表；如果没有与给定 HttpUrl 匹配的 cookie，则返回空列表
     * @throws NullPointerException 如果 {@code url} 为 {@code null}
     * @see #add
     */
    List<Cookie> get(HttpUrl url);

    /**
     * 获取 cookie 存储区中所有未过期的 cookie
     *
     * @return Cookies 的不可变列表；如果存储区中没有 cookie，则返回空列表
     */
    List<Cookie> getCookies();

    /**
     * 从存储区中移除 cookie
     *
     * @param cookie 要移除的 cookie
     * @return 如果此存储区包含指定的 cookie，则返回 {@code true}
     * @throws NullPointerException 如果 cookie 为 {@code null}
     */
    boolean remove(Cookie cookie);

    /**
     * 移除此 cookie 存储区中的所有 cookie
     *
     * @return 如果此存储区由于调用而更改，则返回 {@code true}
     */
    boolean removeAll();
}
