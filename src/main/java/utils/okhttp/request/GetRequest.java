package utils.okhttp.request;

import okhttp3.Request;
import okhttp3.RequestBody;

@SuppressWarnings("unused")
public class GetRequest extends OkHttpRequest {
    protected GetRequest(GetBuilder builder) {
        super(builder);
    }

    @Override
    public GetBuilder newBuilder() {
        return new GetBuilder(this);
    }

    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.get().build();
    }

    @Override
    public String method() {
        return "GET";
    }
}
