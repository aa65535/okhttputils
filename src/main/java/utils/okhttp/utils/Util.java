package utils.okhttp.utils;

import java.util.Collection;
import java.util.Map;

public final class Util {
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
