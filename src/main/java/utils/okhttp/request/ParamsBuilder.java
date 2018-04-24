package utils.okhttp.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import utils.okhttp.utils.Objects;

/**
 * 携带请求参数的 Builder
 * 对于 POST 请求是添加到请求体，对于 GET 请求则添加到 URL 的 query 中
 */
@SuppressWarnings({"unchecked", "unused"})
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
        if (Objects.nonNull(params)) {
            for (Entry<String, String> entry : params.entrySet()) {
                addParam(entry.getKey(), entry.getValue());
            }
        }
        return (T) this;
    }

    /**
     * 添加数组请求参数
     */
    public T addArrayParam(String name, Object[] values) {
        return addArrayParam(name, Arrays.asList(values));
    }

    /**
     * 添加数组请求参数
     */
    public T addArrayParam(String name, List<?> values) {
        for (Object value : values) {
            addParam(name.endsWith("[]") ? name : name + "[]", value);
        }
        return (T) this;
    }

    /**
     * 添加请求参数
     */
    public abstract T addParam(String name, Object value);

    /**
     * 设置请求参数
     */
    public abstract T setParam(String name, Object value);

    /**
     * 添加请求参数， {@code value} 为空时不添加
     */
    public T addOptionParam(String name, Object value) {
        if (!Objects.isEmpty(value)) {
            addParam(name, value);
        }
        return (T) this;
    }

    /**
     * 添加请求参数， {@code value} 不可为空
     */
    public T addNonEmptyParam(String name, Object value) {
        return addParam(name, Objects.requireNonEmpty(value, "params [" + name + "] is empty."));
    }

    /**
     * 添加请求参数， {@code value} 为 {@code null} 时添加空字符串
     */
    public T addNullableParam(String name, Object value) {
        if (Objects.nonNull(value)) {
            return addParam(name, value);
        }
        return addParam(name, "");
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
    @Deprecated
    public T removeAllParam() {
        return clearParams();
    }

    /**
     * 移除所有请求参数
     */
    public T clearParams() {
        params.clear();
        return (T) this;
    }

    /**
     * 按照自然顺序对请求参数排序
     */
    public T sortParams() {
        params.sort();
        return (T) this;
    }

    /**
     * 按照给定的比较器对请求参数排序
     */
    public T sortParams(Comparator<String> comparator) {
        params.sort(comparator);
        return (T) this;
    }

    /**
     * 对请求参数签名，会在请求参数后追加 {@code &sign=xxx}
     * @param signature {@link Signature} 的实现类
     */
    public T signParams(Signature signature) {
        return signParams("sign", signature);
    }

    /**
     * 对请求参数签名，会在请求参数后追加 {@code &name=xxx}
     * @param name 签名的参数名称
     * @param signature  {@link Signature} 的实现类
     */
    public T signParams(String name, Signature signature) {
        addParam(name, signature.sign(params.unmodifiableParams()));
        return (T) this;
    }

    public interface Signature {
        /**
         * 对传入的请求参数进行签名
         * @param params 待签名的请求参数，不可修改的
         * @return 签名后的字符串
         */
        String sign(Params params);
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
            for (int i = namesAndValues.size() - 2; i >= 0; i -= 2) {
                if (name.equals(namesAndValues.get(i))) {
                    namesAndValues.remove(i); // name
                    namesAndValues.remove(i); // value
                }
            }
        }

        /**
         * 根据参数名设置参数值，如果参数名存在则覆盖，不存在则添加
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
         * 按照自然顺序对参数名排序
         */
        public void sort() {
            sort(null);
        }

        /**
         * 按照给定的比较器对参数名排序
         */
        public void sort(Comparator<String> comparator) {
            TreeMap<String, List<String>> temp = new TreeMap<>(comparator);
            for (int i = 0, size = size(); i < size; i++) {
                String name = name(i);
                if (!temp.containsKey(name)) {
                    temp.put(name, new ArrayList<String>(2));
                }
                temp.get(name).add(value(i));
            }
            clear();
            for (Entry<String, List<String>> entry : temp.entrySet()) {
                for (String value : entry.getValue()) {
                    add(entry.getKey(), value);
                }
            }
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
            Set<String> result = new LinkedHashSet<>(size());
            for (int i = 0, size = size(); i < size; i++) {
                result.add(name(i));
            }
            return result.isEmpty()
                    ? Collections.<String>emptySet()
                    : Collections.unmodifiableSet(result);
        }

        /**
         * 根据参数名返回所有的参数值
         *
         * @param name 参数名
         */
        public List<String> values(String name) {
            List<String> result = new ArrayList<>(2);
            for (int i = 0, size = size(); i < size; i++) {
                if (name.equals(name(i))) {
                    result.add(value(i));
                }
            }
            return result.isEmpty()
                    ? Collections.<String>emptyList()
                    : Collections.unmodifiableList(result);
        }

        /**
         * 返回所有参数的名值对
         */
        public List<Entry<String, String>> entryList() {
            List<Entry<String, String>> result = new ArrayList<>(size());
            for (int i = 0, size = size(); i < size; i++) {
                result.add(new Node(name(i), value(i)));
            }
            return result.isEmpty()
                    ? Collections.<Entry<String, String>>emptyList()
                    : Collections.unmodifiableList(result);
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
