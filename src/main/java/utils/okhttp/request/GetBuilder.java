package utils.okhttp.request;

import java.util.Map.Entry;

import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import utils.okhttp.utils.Objects;

@SuppressWarnings("unused")
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
        HttpUrl httpUrl = HttpUrl.parse(Objects.requireNonNull(url, "url is null."));
        httpUrlBuilder = Objects.requireNonNull(httpUrl,
                new IllegalArgumentException("unexpected url: " + url)).newBuilder();
        //noinspection ConstantConditions
        for (String name : httpUrl.queryParameterNames()) {
            httpUrlBuilder.removeAllQueryParameters(name);
            for (String value : httpUrl.queryParameterValues(name)) {
                addParam(name, value);
            }
        }
        return this;
    }

    /**
     * 添加请求参数， {@code value} 为 {@code null} 时添加为 &name，否则为 &name=value
     */
    @Override
    public GetBuilder addParam(String name, Object value) {
        params.add(name, Objects.isNull(value) ? null : value.toString());
        return this;
    }

    /**
     * 设置请求参数， {@code value} 为 {@code null} 时设置为 &name，否则为 &name=value
     */
    @Override
    public GetBuilder setParam(String name, Object value) {
        params.set(name, Objects.isNull(value) ? null : value.toString());
        return this;
    }

    /**
     * 获取请求的最终 URL
     */
    protected String getUrl() {
        Objects.requireNonNull(httpUrlBuilder, new IllegalStateException("url not set."));
        for (Entry<String, String> entry : params.entryList()) {
            httpUrlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return httpUrlBuilder.build().toString();
    }
}
