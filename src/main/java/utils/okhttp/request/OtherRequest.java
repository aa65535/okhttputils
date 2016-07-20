package utils.okhttp.request;

import okhttp3.Request;
import okhttp3.RequestBody;
import utils.okhttp.utils.Objects;

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
        requestBody = Objects.getDefinedObject(requestBody, RequestBody.create(null, new byte[0]));
        return builder.method(method, requestBody).build();
    }

    /**
     * 返回当前实例的 {@code method}
     */
    public String method() {
        return method;
    }
}
