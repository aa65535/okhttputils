package utils.okhttp.request;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import utils.okhttp.OkHttpUtils;

public class PostFileRequest extends OkHttpRequest {
    File file;
    MediaType mediaType;

    PostFileRequest(PostFileBuilder builder) {
        super(builder);
        this.file = builder.file;
        this.mediaType = builder.mediaType;
    }

    @Override
    public PostFileBuilder newBuilder() {
        return new PostFileBuilder(this);
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(mediaType, file);
    }

    @Override
    protected RequestBody wrapRequestBody(RequestBody requestBody) {
        if (callback == null)
            return requestBody;

        return new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength) {
                OkHttpUtils.getInstance().getThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.inProgress(bytesWritten, contentLength);
                    }
                });
            }
        });
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.post(requestBody).build();
    }
}
