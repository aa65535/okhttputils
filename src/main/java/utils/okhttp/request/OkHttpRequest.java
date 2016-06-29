package utils.okhttp.request;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import utils.okhttp.callback.Callback;
import utils.okhttp.utils.Util;

public abstract class OkHttpRequest {
    protected Request.Builder builder;

    protected OkHttpRequest(String url, Object tag, Map<String, String> headers) {
        if (Util.isEmpty(url))
            throw new IllegalArgumentException("url can not be empty.");
        this.builder = new Request.Builder().url(url).tag(tag);
        appendHeaders(headers);
    }

    protected abstract RequestBody buildRequestBody();

    protected abstract Request buildRequest(RequestBody requestBody);

    protected RequestBody wrapRequestBody(RequestBody requestBody, final Callback callback) {
        return requestBody;
    }

    public RequestCall build() {
        return new RequestCall(this);
    }

    public Request generateRequest(Callback callback) {
        RequestBody requestBody = buildRequestBody();
        RequestBody wrappedRequestBody = wrapRequestBody(requestBody, callback);
        return buildRequest(wrappedRequestBody);
    }

    protected void appendHeaders(Map<String, String> headers) {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (Util.isEmpty(headers))
            return;
        for (String key : headers.keySet())
            headerBuilder.add(key, headers.get(key));
        builder.headers(headerBuilder.build());
    }
}
