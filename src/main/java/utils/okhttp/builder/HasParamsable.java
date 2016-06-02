package utils.okhttp.builder;

import java.util.Map;

public interface HasParamsable {
    OkHttpRequestBuilder params(Map<String, String> params);

    OkHttpRequestBuilder addParams(String key, String val);
}
