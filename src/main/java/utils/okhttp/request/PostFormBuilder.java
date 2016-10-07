package utils.okhttp.request;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import utils.okhttp.utils.Objects;

@SuppressWarnings("unused")
public class PostFormBuilder extends ParamsBuilder<PostFormBuilder> {
    protected List<FileInput> files;

    public PostFormBuilder() {
        this.files = new ArrayList<>();
    }

    protected PostFormBuilder(PostFormRequest request) {
        super(request);
        this.files = request.files;
        this.params(request.params);
    }

    @Override
    public PostFormRequest build() {
        return new PostFormRequest(this);
    }

    /**
     * 添加请求参数， {@code value} 不可为 {@code null}
     */
    @Override
    public PostFormBuilder addParam(String name, Object value) {
        params.add(name, Objects.requireNonNull(value, "params [" + name + "] is null.").toString());
        return this;
    }

    /**
     * 添加一个待提交的文件映射表
     *
     * @param name  From 字段名称
     * @param files 文件映射表, Key 代表文件名 Value 代表文件
     */
    public PostFormBuilder addFiles(String name, Map<String, File> files) {
        if (Objects.nonNull(files)) {
            this.files.clear();
            for (Entry<String, File> entry : files.entrySet())
                addFile(name, entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * 添加一个待提交的文件
     *
     * @param name     From 字段名称
     * @param filename 文件名
     * @param file     文件
     */
    public PostFormBuilder addFile(String name, String filename, File file) {
        files.add(new FileInput(name, filename, file));
        return this;
    }

    public static class FileInput {
        public String name;
        public String filename;
        public File file;

        public FileInput(String name, String filename, File file) {
            this.name = name;
            this.filename = filename;
            this.file = file;
        }

        public RequestBody fileBody() {
            return RequestBody.create(getMediaType(filename, file), file);
        }

        public static MediaType getMediaType(String filename, File file) {
            try {
                FileNameMap fileNameMap = URLConnection.getFileNameMap();
                String name = Objects.getDefinedObject(filename, file.getName());
                String contentType = fileNameMap.getContentTypeFor(URLEncoder.encode(name, "UTF-8"));
                if (Objects.nonNull(contentType))
                    return MediaType.parse(contentType);
            } catch (Exception ignored) {
            }
            return MEDIA_TYPE_STREAM;
        }

        @Override
        public String toString() {
            return "FileInput{" +
                    "name='" + name + '\'' +
                    ", filename='" + filename + '\'' +
                    ", file=" + file +
                    '}';
        }
    }
}
