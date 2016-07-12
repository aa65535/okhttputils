package utils.okhttp.request;

import java.io.File;

import okhttp3.MediaType;
import utils.okhttp.utils.Constants;
import utils.okhttp.utils.Objects;

public class PostFileBuilder extends OkHttpBuilder<PostFileBuilder> implements Constants {
    protected File file;
    protected MediaType mediaType;

    public PostFileBuilder() {
        this.mediaType = MEDIA_TYPE_STREAM;
    }

    protected PostFileBuilder(PostFileRequest request) {
        super(request);
        this.file(request.file);
        this.mediaType(request.mediaType);
    }

    @Override
    public PostFileRequest build() {
        Objects.requireNonNull(file, "the file can not be null.");
        return new PostFileRequest(this);
    }

    /**
     * 设置待上传的文件
     */
    public OkHttpBuilder file(File file) {
        this.file = file;
        return this;
    }

    /**
     * 设置请求的 {@link MediaType}，默认是 {@code application/octet-stream}
     */
    public OkHttpBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }
}
