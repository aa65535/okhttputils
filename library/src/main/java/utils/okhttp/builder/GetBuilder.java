package utils.okhttp.builder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import utils.okhttp.request.GetRequest;
import utils.okhttp.request.RequestCall;

public class GetBuilder extends OkHttpRequestBuilder<GetBuilder> implements HasParamsable {
    @Override
    public RequestCall build() {
        if (params != null) {
            url = appendParams(url, params);
        }
        return new GetRequest(url, tag, params, headers).build();
    }

    protected String appendParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Builder builder = HttpUrl.parse(url).newBuilder();
        for (Entry<String, String> entry : params.entrySet()) {
            builder.addEncodedQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build().toString();
    }

    @Override
    public GetBuilder params(Map<String, String> params) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl.querySize() > 0) {
            Builder builder = httpUrl.newBuilder();
            for (String name : httpUrl.queryParameterNames()) {
                builder.removeAllQueryParameters(name);
            }
            this.url = builder.build().toString();
        }
        this.params = params;
        return this;
    }

    @Override
    public GetBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    public GetBuilder addParams(Map<String, String> params) {
        if (null != params) {
            if (this.params == null) {
                this.params = params;
            } else {
                this.params.putAll(params);
            }
        }
        return this;
    }
}
