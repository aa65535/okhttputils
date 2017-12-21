package utils.okhttp.request;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import utils.okhttp.OkHttpUtils;
import utils.okhttp.request.ParamsBuilder.Params;
import utils.okhttp.request.PostFormBuilder.FileInput;

@SuppressWarnings("unused")
public class PostFormRequest extends OkHttpRequest {
    protected Params params;
    protected List<FileInput> files;

    protected PostFormRequest(PostFormBuilder builder) {
        super(builder);
        this.files = builder.files;
        this.params = builder.params;
    }

    @Override
    public PostFormBuilder newBuilder() {
        return new PostFormBuilder(this);
    }

    @Override
    public String method() {
        return "POST";
    }

    @Override
    protected RequestBody buildRequestBody() {
        if (files.isEmpty()) {
            return newFormBody();
        }
        return newMultipartBody();
    }

    private RequestBody newFormBody() {
        FormBody.Builder builder = new FormBody.Builder();
        for (Entry<String, String> entry : params.entryList()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    private RequestBody newMultipartBody() {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Entry<String, String> entry : params.entryList()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        for (FileInput input : files) {
            builder.addFormDataPart(input.name, input.filename, input.fileBody());
        }
        return builder.build();
    }

    @Override
    protected RequestBody wrapRequestBody(RequestBody requestBody) {
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

    /**
     * 返回文件列表
     */
    public List<FileInput> files() {
        return Collections.unmodifiableList(files);
    }

    /**
     * 返回请求参数不可修改版本
     */
    public Params params() {
        return params.unmodifiableParams();
    }
}
