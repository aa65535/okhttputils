package utils.okhttp.request;

import okhttp3.Request;
import okhttp3.RequestBody;
import utils.okhttp.utils.Method;

public class OtherRequest extends OkHttpRequest {
    String method;
    RequestBody requestBody;

    OtherRequest(OtherBuilder builder) {
        super(builder);
        this.method = builder.method;
        this.requestBody = builder.requestBody;
    }

    @Override
    public OtherBuilder newBuilder() {
        return new OtherBuilder(this);
    }

    @Override
    protected RequestBody buildRequestBody() {
        return requestBody;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        switch (method) {
            case Method.PUT:
                builder.put(requestBody);
                break;
            case Method.DELETE:
                if (requestBody == null)
                    builder.delete();
                else
                    builder.delete(requestBody);
                break;
            case Method.PATCH:
                builder.patch(requestBody);
                break;
        }
        return builder.build();
    }
}
