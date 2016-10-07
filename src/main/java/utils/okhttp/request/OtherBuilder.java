package utils.okhttp.request;

import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;
import utils.okhttp.utils.Objects;

@SuppressWarnings("unused")
public class OtherBuilder extends OkHttpBuilder<OtherBuilder> {
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
            throw new NullPointerException(
                    String.format("requestBody can not be null in method: %s.", method));
        return new OtherRequest(this);
    }

    public OtherBuilder requestBody(String content) {
        return requestBody(RequestBody.create(MEDIA_TYPE_PLAIN,
                Objects.requireNonNull(content, "content is null.")));
    }

    public OtherBuilder requestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }
}
