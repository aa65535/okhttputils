package utils.okhttp.request;

import okhttp3.Request;
import okhttp3.RequestBody;
import utils.okhttp.utils.Method;

public class OtherRequest extends OkHttpRequest {
    protected String method;
    protected RequestBody requestBody;

    protected OtherRequest(OtherBuilder builder) {
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

    /**
     * 返回当前实例的 {@link #method}
     */
    public String method() {
        return method;
    }
}
