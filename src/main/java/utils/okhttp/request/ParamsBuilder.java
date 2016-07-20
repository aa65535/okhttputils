package utils.okhttp.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import utils.okhttp.utils.Objects;

/**
 * 携带请求参数的 Builder
 * 对于 POST 请求是添加到请求体，对于 GET 请求则添加到 URL 的 query 中
 */
@SuppressWarnings("unchecked")
public abstract class ParamsBuilder<T extends ParamsBuilder> extends OkHttpBuilder<T> {
    protected Params params;

    public ParamsBuilder() {
        this.params = new Params();
    }

    protected ParamsBuilder(OkHttpRequest request) {
        super(request);
    }

    /**
     * 设置请求参数
     */
    public T params(Params params) {
        this.params = params;
        return (T) this;
    }

    /**
     * 设置请求参数
     */
    public T params(Map<String, String> params) {
        this.params.clear();
        return addParams(params);
    }

    /**
     * 添加请求参数
     */
    public T addParams(Map<String, String> params) {
        if (Objects.nonNull(params))
            for (Entry<String, String> entry : params.entrySet())
                _addParam(entry.getKey(), entry.getValue());
        return (T) this;
    }

    /**
     * 添加请求参数， {@code value} 不可为 {@code null}
     */
    public T addParam(String name, Object value) {
        return _addParam(name, Objects.requireNonNull(value, "params [" + name + "] is null."));
    }

    /**
     * 设置请求参数， {@code value} 不可为 {@code null}
     */
    public T setParam(String name, Object value) {
        return _setParam(name, Objects.requireNonNull(value, "params [" + name + "] is null."));
    }

    /**
     * 添加请求参数， {@code value} 为空时不添加
     */
    public T addOptionParam(String name, Object value) {
        if (!Objects.isEmpty(value))
            _addParam(name, value);
        return (T) this;
    }

    /**
     * 添加请求参数， {@code value} 不可为空
     */
    public T addNonEmptyParam(String name, Object value) {
        return _addParam(name, Objects.requireNonEmpty(value, "params [" + name + "] is empty."));
    }

    /**
     * 添加请求参数， {@code value} 为 {@code null} 时添加空字符串
     */
    public T addNullableParam(String name, Object value) {
        if (Objects.nonNull(value))
            return _addParam(name, value);
        return _addParam(name, "");
    }

    /**
     * 移除给定 {@code name} 的请求参数
     */
    public T removeParam(String name) {
        params.removeAll(name);
        return (T) this;
    }

    /**
     * 移除所有请求参数
     */
    public T removeAllParam() {
        params.clear();
        return (T) this;
    }

    private T _setParam(String name, Object value) {
        params.set(name, value.toString());
        return (T) this;
    }

    private T _addParam(String name, Object value) {
        params.add(name, value.toString());
        return (T) this;
    }

    /**
     * 允许名称相同的请求参数
     */
    public static final class Params {
        private final List<String> namesAndValues;

        public Params() {
            this.namesAndValues = new ArrayList<>(20);
        }

        private Params(List<String> namesAndValues) {
            this.namesAndValues = namesAndValues;
        }

        public Params unmodifiableParams() {
            return new Params(Collections.unmodifiableList(namesAndValues));
        }

        /**
         * 添加一个参数
         *
         * @param name  参数名
         * @param value 参数值
         */
        public void add(String name, String value) {
            namesAndValues.add(name);
            namesAndValues.add(value);
        }

        /**
         * 根据参数名移除所有的对应参数
         *
         * @param name 参数名
         */
        public void removeAll(String name) {
            for (int i = 0; i < namesAndValues.size(); i += 2) {
                if (name.equals(namesAndValues.get(i))) {
                    namesAndValues.remove(i); // name
                    namesAndValues.remove(i); // value
                    i -= 2;
                }
            }
        }

        /**
         * 根据参数名设置参数值，此操作会覆盖先移除所有对应的参数值，然后添加
         *
         * @param name  参数名
         * @param value 参数值
         */
        public void set(String name, String value) {
            removeAll(name);
            add(name, value);
        }

        /**
         * 根据参数名获取对应的参数值，如果有多个参数值则返回最后添加的
         *
         * @param name 参数名
         */
        public String get(String name) {
            for (int i = namesAndValues.size() - 2; i >= 0; i -= 2) {
                if (name.equals(namesAndValues.get(i))) {
                    return namesAndValues.get(i + 1);
                }
            }
            return null;
        }

        /**
         * 清空所有参数
         */
        public void clear() {
            namesAndValues.clear();
        }

        /**
         * 参数数量
         */
        public int size() {
            return namesAndValues.size() / 2;
        }

        /**
         * 根据索引返回对应的参数名
         *
         * @param index 索引
         */
        public String name(int index) {
            return namesAndValues.get(index * 2);
        }

        /**
         * 根据索引返回对应的参数值
         *
         * @param index 索引
         */
        public String value(int index) {
            return namesAndValues.get(index * 2 + 1);
        }

        /**
         * 返回由所有参数名所组成的 Set
         */
        public Set<String> names() {
            TreeSet<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            for (int i = 0, size = size(); i < size; i++) {
                result.add(name(i));
            }
            return Collections.unmodifiableSet(result);
        }

        /**
         * 根据参数名返回所有的参数值
         *
         * @param name 参数名
         */
        public List<String> values(String name) {
            List<String> result = null;
            for (int i = 0, size = size(); i < size; i++) {
                if (name.equals(name(i))) {
                    if (result == null)
                        result = new ArrayList<>(2);
                    result.add(value(i));
                }
            }
            return result != null
                    ? Collections.unmodifiableList(result)
                    : Collections.<String>emptyList();
        }

        /**
         * 返回所有参数的名值对
         */
        public List<Entry<String, String>> entryList() {
            List<Entry<String, String>> result = new ArrayList<>(size());
            for (int i = 0, size = size(); i < size; i++)
                result.add(new Node(name(i), value(i)));
            return Collections.unmodifiableList(result);
        }

        static class Node implements Entry<String, String> {
            final String key;
            String value;

            Node(String key, String value) {
                this.key = key;
                this.value = value;
            }

            @Override
            public String getKey() {
                return key;
            }

            @Override
            public String getValue() {
                return value;
            }

            @Override
            public String setValue(String value) {
                String oldValue = this.value;
                this.value = value;
                return oldValue;
            }
        }
    }
}
