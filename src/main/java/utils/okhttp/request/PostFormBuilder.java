package utils.okhttp.request;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import utils.okhttp.utils.Constants;
import utils.okhttp.utils.Objects;

public class PostFormBuilder extends ParamsBuilder<PostFormBuilder> {
    protected List<FileInput> files;

    public PostFormBuilder() {
        files = new ArrayList<>();
    }

    protected PostFormBuilder(PostFormRequest request) {
        super(request);
        this.params = request.params;
        this.files = request.files;
    }

    @Override
    public PostFormRequest build() {
        return new PostFormRequest(this);
    }

    /**
     * 设置待提交的文件列表
     */
    public PostFormBuilder files(String name, Map<String, File> files) {
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
                String contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(name, "UTF-8"));
                return MediaType.parse(contentTypeFor);
            } catch (UnsupportedEncodingException ignored) {
            }
            return Constants.MEDIA_TYPE_STREAM;
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
