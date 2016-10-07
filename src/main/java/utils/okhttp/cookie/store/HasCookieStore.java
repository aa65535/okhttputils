package utils.okhttp.cookie.store;

@SuppressWarnings("unused")
public interface HasCookieStore {
    /**
     * 获取 {@link CookieStore} 对象
     */
    CookieStore getCookieStore();
}
