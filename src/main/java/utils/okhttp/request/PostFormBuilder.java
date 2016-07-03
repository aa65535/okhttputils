package utils.okhttp.request;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostFormBuilder extends ParamsBuilder<PostFormBuilder> {
    List<FileInput> files;

    public PostFormBuilder() {
        files = new ArrayList<>();
    }

    PostFormBuilder(PostFormRequest request) {
        super(request);
        this.params = request.params;
        this.files = request.files;
    }

    @Override
    public PostFormRequest build() {
        return new PostFormRequest(this);
    }

    public PostFormBuilder files(String key, Map<String, File> files) {
        for (String filename : files.keySet())
            this.files.add(new FileInput(key, filename, files.get(filename)));
        return this;
    }

    public PostFormBuilder addFile(String name, String filename, File file) {
        files.add(new FileInput(name, filename, file));
        return this;
    }

    public static class FileInput {
        public String key;
        public String filename;
        public File file;

        public FileInput(String name, String filename, File file) {
            this.key = name;
            this.filename = filename;
            this.file = file;
        }

        @Override
        public String toString() {
            return "FileInput{" +
                    "key='" + key + '\'' +
                    ", filename='" + filename + '\'' +
                    ", file=" + file +
                    '}';
        }
    }
}
