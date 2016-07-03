package utils.okhttp.request;

import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import utils.okhttp.OkHttpUtils;
import utils.okhttp.request.PostFormBuilder.FileInput;
import utils.okhttp.utils.Constants;

public class PostFormRequest extends OkHttpRequest {
    volatile Map<String, String> params;
    List<FileInput> files;

    PostFormRequest(PostFormBuilder builder) {
        super(builder);
        this.params = builder.params;
        this.files = builder.files;
    }

    @Override
    public PostFormBuilder newBuilder() {
        return new PostFormBuilder(this);
    }

    @Override
    protected RequestBody buildRequestBody() {
        if (files == null || files.isEmpty()) {
            FormBody.Builder builder = new FormBody.Builder();
            addParams(builder);
            return builder.build();
        } else {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            addParams(builder);
            for (FileInput input : files) {
                RequestBody fileBody = RequestBody.create(guessMimeType(input.filename), input.file);
                builder.addFormDataPart(input.key, input.filename, fileBody);
            }
            return builder.build();
        }
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

    private MediaType guessMimeType(String path) {
        try {
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
            return MediaType.parse(contentTypeFor);
        } catch (UnsupportedEncodingException ignored) {
        }
        return Constants.MEDIA_TYPE_STREAM;
    }

    private void addParams(MultipartBody.Builder builder) {
        if (params != null && !params.isEmpty())
            for (String key : params.keySet())
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));
    }

    private void addParams(FormBody.Builder builder) {
        if (params != null)
            for (String key : params.keySet())
                builder.add(key, params.get(key));
    }
}
