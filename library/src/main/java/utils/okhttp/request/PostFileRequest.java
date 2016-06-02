package utils.okhttp.request;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import utils.okhttp.OkHttpUtils;
import utils.okhttp.callback.Callback;

public class PostFileRequest extends OkHttpRequest {
    private static MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");

    private File file;
    private MediaType mediaType;

    public PostFileRequest(String url, Object tag, Map<String, String> params,
                           Map<String, String> headers, File file, MediaType mediaType) {
        super(url, tag, params, headers);
        this.file = file;
        this.mediaType = mediaType;

        if (this.file == null) {
            throw new IllegalArgumentException("the file can not be null !");
        }
        if (this.mediaType == null) {
            this.mediaType = MEDIA_TYPE_STREAM;
        }
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(mediaType, file);
    }

    @Override
    protected RequestBody wrapRequestBody(RequestBody requestBody, final Callback callback) {
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
