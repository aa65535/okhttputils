package utils.okhttp.request;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
        if (null != files) {
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
