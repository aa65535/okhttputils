package utils.okhttp.request;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PostStringRequest extends OkHttpRequest {
    String content;
    MediaType mediaType;

    PostStringRequest(PostStringBuilder builder) {
        super(builder);
        this.content = builder.content;
        this.mediaType = builder.mediaType;
    }

    @Override
    public PostStringBuilder newBuilder() {
        return new PostStringBuilder(this);
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(mediaType, content);
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.post(requestBody).build();
    }
}
