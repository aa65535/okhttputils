package utils.okhttp.request;

import okhttp3.Request;
import okhttp3.RequestBody;

public class GetRequest extends OkHttpRequest {
    GetRequest(GetBuilder builder) {
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
}
