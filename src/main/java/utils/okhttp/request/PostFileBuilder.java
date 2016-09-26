package utils.okhttp.request;

import java.io.File;

import okhttp3.MediaType;
import utils.okhttp.utils.Objects;

public class PostFileBuilder extends OkHttpBuilder<PostFileBuilder> {
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
        Objects.requireNonNull(file, "file is null.");
        return new PostFileRequest(this);
    }

    /**
     * 设置待上传的文件
     */
    public PostFileBuilder file(File file) {
        this.file = file;
        return this;
    }

    /**
     * 设置请求的 {@link MediaType}，默认是 {@code application/octet-stream}
     */
    public PostFileBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }
}
