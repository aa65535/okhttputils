package utils.okhttp.request;

import okhttp3.MediaType;
import utils.okhttp.utils.Constants;
import utils.okhttp.utils.Objects;

public class PostStringBuilder extends OkHttpBuilder<PostStringBuilder> implements Constants {
    protected String content;
    protected MediaType mediaType;

    public PostStringBuilder() {
        this.mediaType = MEDIA_TYPE_PLAIN;
    }

    protected PostStringBuilder(PostStringRequest request) {
        super(request);
        this.content = request.content;
        this.mediaType = request.mediaType;
    }

    @Override
    public PostStringRequest build() {
        Objects.requireNonNull(content, "the content can not be null!");
        return new PostStringRequest(this);
    }

    /**
     * 设置待提交的字符串
     */
    public PostStringBuilder content(String content) {
        this.content = content;
        return this;
    }

    /**
     * 设置请求的 {@link MediaType}，默认是 {@code text/plain;charset=utf-8}
     */
    public PostStringBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }
}
