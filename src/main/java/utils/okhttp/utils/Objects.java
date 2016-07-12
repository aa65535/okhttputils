package utils.okhttp.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public final class Objects {
    /**
     * 判断两个对象是否相等
     */
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    /**
     * 返回对象的哈希码值
     */
    public static int hashCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }

    /**
     * 判断对象是否为 {@code null}
     *
     * @param obj 待检查的对象
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 判断对象是否不为 {@code null}
     *
     * @param obj 待检查的对象
     */
    public static boolean nonNull(Object obj) {
        return obj != null;
    }

    /**
     * 检查一个对象是否是空，满足以下条件将返回 {@code true}
     * <br/> 当 待检查的对象为 {@code null}
     * <br/> 当 {@link Number} 值为 0
     * <br/> 当 {@link CharSequence} 长度为 0
     * <br/> 当 {@link Collection} 为空
     * <br/> 当 {@link Map} 为空
     * <br/> 当 数组 长度为 0
     *
     * @param object 待检查的对象
     */
    public static boolean isEmpty(Object object) {
        if (isNull(object))
            return true;
        if (object instanceof CharSequence)
            return 0 == ((CharSequence) object).length();
        if (object instanceof Number)
            return 0 == ((Number) object).doubleValue();
        if (object instanceof Collection)
            return ((Collection) object).isEmpty();
        if (object instanceof Map)
            return ((Map) object).isEmpty();
        return object.getClass().isArray() && 0 == Array.getLength(object);
    }

    /**
     * 获取一个对象，如果待检查的对象为 {@code null} 则返回默认值
     *
     * @param obj          待检查的对象
     * @param defaultValue 默认值
     */
    public static <T> T getDefinedObject(T obj, T defaultValue) {
        return nonNull(obj) ? obj : defaultValue;
    }

    /**
     * 检查一个对象是否为 {@code null}，如果为 {@code null}，
     * 则抛出 {@link NullPointerException}，否则返回此对象
     *
     * @param obj 待检查的对象
     */
    public static <T> T requireNonNull(T obj) {
        return requireNonNull(obj, null);
    }

    /**
     * 检查一个对象是否为 {@code null}，如果为 {@code null}，
     * 则抛出带消息的 {@link NullPointerException}，否则返回此对象
     *
     * @param obj 待检查的对象
     */
    public static <T> T requireNonNull(T obj, String message) {
        if (isNull(obj))
            throw new NullPointerException(message);
        return obj;
    }

    /**
     * 检查一个对象是否为空，如果为空，则抛出 {@link NullPointerException}，否则返回此对象
     *
     * @param obj 待检查的对象
     */
    public static <T> T requireNonEmpty(T obj) {
        return requireNonEmpty(obj, null);
    }

    /**
     * 检查一个对象是否为空，如果为空，则抛出带消息的 {@link IllegalArgumentException}，否则返回此对象
     *
     * @param obj 待检查的对象
     */
    public static <T> T requireNonEmpty(T obj, String message) {
        if (isEmpty(obj))
            throw new IllegalArgumentException(message);
        return obj;
    }
}
