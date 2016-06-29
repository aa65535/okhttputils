package utils.okhttp.builder;

import java.util.Map;
import java.util.Map.Entry;

import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import utils.okhttp.request.GetRequest;
import utils.okhttp.request.RequestCall;
import utils.okhttp.utils.Util;

public class GetBuilder extends ParamsBuilder<GetBuilder> {
    @Override
    public RequestCall build() {
        if (params != null)
            url = appendParams(url, params);
        return new GetRequest(url, tag, headers).build();
    }

    protected String appendParams(String url, Map<String, String> params) {
        if (Util.isEmpty(params))
            return url;

        Builder builder = HttpUrl.parse(url).newBuilder();

        for (Entry<String, String> entry : params.entrySet())
            builder.addEncodedQueryParameter(entry.getKey(), entry.getValue());

        return builder.build().toString();
    }

    @Override
    public GetBuilder params(Map<String, String> params) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl.querySize() > 0) {
            Builder builder = httpUrl.newBuilder();
            for (String name : httpUrl.queryParameterNames())
                builder.removeAllQueryParameters(name);

            this.url = builder.build().toString();
        }
        return super.params(params);
    }
}
