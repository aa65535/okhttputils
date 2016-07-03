package utils.okhttp.request;

import okhttp3.Request;
import okhttp3.RequestBody;

public class HeadRequest extends GetRequest {
    HeadRequest(HeadBuilder builder) {
        super(builder);
    }

    @Override
    public HeadBuilder newBuilder() {
        return new HeadBuilder(this);
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.head().build();
    }
}
