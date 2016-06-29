package utils.okhttp.builder;

import java.io.File;

import okhttp3.MediaType;
import utils.okhttp.request.PostFileRequest;
import utils.okhttp.request.RequestCall;

public class PostFileBuilder extends OkHttpRequestBuilder<PostFileBuilder> {
    private File file;
    private MediaType mediaType;

    public OkHttpRequestBuilder file(File file) {
        this.file = file;
        return this;
    }

    public OkHttpRequestBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostFileRequest(url, tag, headers, file, mediaType).build();
    }
}
