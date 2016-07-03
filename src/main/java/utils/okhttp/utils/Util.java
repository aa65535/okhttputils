package utils.okhttp.utils;

import java.util.Collection;
import java.util.Map;

public final class Util {
    /**
     * 检查一个对象是否是空，满足以下条件将返回 {@code true}
     * <br/> 当此对象为 {@code null}
     * <br/> 当 {@link Number} 值为 0
     * <br/> 当 {@link Collection} 为空
     * <br/> 当 {@link Map} 为空
     * <br/> 当 {@link #toString} 后的字符串为空
     *
     * @param object 待检查的对象
     */
    public static boolean isEmpty(Object object) {
        if (null == object)
            return true;
        if (object instanceof Number)
            return 0 == ((Number) object).doubleValue();
        if (object instanceof Collection)
            return ((Collection) object).isEmpty();
        if (object instanceof Map)
            return ((Map) object).isEmpty();
        return 0 == object.toString().length();
    }
}
