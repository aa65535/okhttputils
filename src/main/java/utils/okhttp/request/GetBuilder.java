package utils.okhttp.request;

import java.util.Map;
import java.util.Map.Entry;

import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;

public class GetBuilder extends ParamsBuilder<GetBuilder> {
    private HttpUrl httpUrl;
    private Builder httpUrlBuilder;

    public GetBuilder() {
    }

    GetBuilder(GetRequest request) {
        super(request);
        this.url(request.url);
    }

    @Override
    public GetRequest build() {
        this.url = getUrl();
        return new GetRequest(this);
    }

    @Override
    public GetBuilder url(String url) {
        this.httpUrl = HttpUrl.parse(url);
        this.httpUrlBuilder = httpUrl.newBuilder();
        return this;
    }

    @Override
    public GetBuilder params(Map<String, String> params) {
        removeAllParam();
        return addParams(params);
    }

    @Override
    public GetBuilder addParams(Map<String, String> params) {
        if (null != params) {
            for (Entry<String, String> entry : params.entrySet())
                httpUrlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * 设置指定 {@code name} 的请求参数值
     */
    public GetBuilder setParam(String name, Object value) {
        httpUrlBuilder.setQueryParameter(name, null == value ? null : value.toString());
        return this;
    }

    /**
     * 添加请求参数， {@code value} 为 {@code null} 时添加空字符串
     */
    @Override
    public GetBuilder addParam(String name, Object value) {
        return addNullableParam(name, value);
    }

    @Override
    public GetBuilder removeParam(String name) {
        httpUrlBuilder.removeAllQueryParameters(name);
        return this;
    }

    @Override
    public GetBuilder removeAllParam() {
        if (httpUrl.querySize() > 0) {
            for (String name : httpUrl.queryParameterNames())
                httpUrlBuilder.removeAllQueryParameters(name);
        }
        return this;
    }

    @Override
    protected void putParam(String name, Object value) {
        httpUrlBuilder.addQueryParameter(name, value.toString());
    }

    /**
     * 获取请求的最终 URL
     */
    protected String getUrl() {
        return httpUrlBuilder.toString();
    }
}
