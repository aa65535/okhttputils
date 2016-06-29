package utils.okhttp.builder;

import okhttp3.MediaType;
import utils.okhttp.request.PostStringRequest;
import utils.okhttp.request.RequestCall;

public class PostStringBuilder extends OkHttpRequestBuilder<PostStringBuilder> {
    private String content;
    private MediaType mediaType;

    public PostStringBuilder content(String content) {
        this.content = content;
        return this;
    }

    public PostStringBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostStringRequest(url, tag, headers, content, mediaType).build();
    }
}
