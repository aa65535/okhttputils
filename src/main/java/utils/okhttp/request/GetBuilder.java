package utils.okhttp.request;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import utils.okhttp.utils.Objects;

public class GetBuilder extends ParamsBuilder<GetBuilder> {
    protected Builder httpUrlBuilder;

    public GetBuilder() {
    }

    protected GetBuilder(GetRequest request) {
        super(request);
    }

    @Override
    public GetRequest build() {
        url = getUrl();
        return new GetRequest(this);
    }

    @Override
    public GetBuilder url(String url) {
        if (Objects.isNull(params))
            params = new LinkedHashMap<>();
        HttpUrl httpUrl = HttpUrl.parse(url);
        httpUrlBuilder = httpUrl.newBuilder();
        for (String name : httpUrl.queryParameterNames()) {
            httpUrlBuilder.removeAllQueryParameters(name);
            addParam(name, httpUrl.queryParameter(name));
        }
        return this;
    }

    /**
     * 设置指定 {@code name} 的请求参数值
     */
    public GetBuilder setParam(String name, Object value) {
        return super.addParam(name, value);
    }

    /**
     * 添加请求参数， {@code value} 为 {@code null} 时添加空字符串
     */
    @Override
    public GetBuilder addParam(String name, Object value) {
        return addNullableParam(name, value);
    }

    /**
     * 获取请求的最终 URL
     */
    protected String getUrl() {
        Objects.requireNonNull(httpUrlBuilder, "the url can not be null.");
        for (Entry<String, String> entry : params.entrySet())
            httpUrlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        return httpUrlBuilder.build().toString();
    }
}
