package utils.okhttp.request;

import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;
import utils.okhttp.utils.Constants;
import utils.okhttp.utils.Objects;

public class OtherBuilder extends OkHttpBuilder<OtherBuilder> implements Constants {
    protected String method;
    protected RequestBody requestBody;

    public OtherBuilder(String method) {
        this.method = method;
    }

    protected OtherBuilder(OtherRequest request) {
        super(request);
        this.method = request.method;
        this.requestBody(request.requestBody);
    }

    @Override
    public OtherRequest build() {
        if (Objects.isNull(requestBody) && HttpMethod.requiresRequestBody(method))
            throw new NullPointerException("requestBody and content can not be null in method:" + method);
        return new OtherRequest(this);
    }

    public OtherBuilder requestBody(String content) {
        if (Objects.nonNull(content))
            requestBody = RequestBody.create(MEDIA_TYPE_PLAIN, content);
        return this;
    }

    public OtherBuilder requestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }
}
