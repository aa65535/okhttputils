package utils.okhttp.request;

import okhttp3.MediaType;
import utils.okhttp.utils.Constants;

public class PostStringBuilder extends OkHttpBuilder<PostStringBuilder> implements Constants {
    String content;
    MediaType mediaType;

    public PostStringBuilder() {
        this.mediaType = MEDIA_TYPE_PLAIN;
    }

    PostStringBuilder(PostStringRequest request) {
        super(request);
        this.content = request.content;
        this.mediaType = request.mediaType;
    }

    @Override
    public PostStringRequest build() {
        if (this.content == null)
            throw new NullPointerException("the content can not be null !");
        return new PostStringRequest(this);
    }

    public PostStringBuilder content(String content) {
        this.content = content;
        return this;
    }

    public PostStringBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }
}
