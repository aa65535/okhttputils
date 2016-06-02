package utils.okhttp.callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;
import utils.okhttp.OkHttpUtils;

public abstract class FileCallBack extends Callback<File> {
    /**
     * 目标文件存储的文件夹路径
     */
    private String destFileDir;
    /**
     * 目标文件存储的文件名
     */
    private String destFileName;

    public FileCallBack(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    /**
     * UI Thread
     */
    public abstract void onGetFileSize(long size);

    /**
     * UI Thread
     */
    public abstract void inProgress(long progress, long size);

    @Override
    public File parseNetworkResponse(Response response) throws Exception {
        return saveFile(response);
    }

    public File saveFile(Response response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0;
            OkHttpUtils.getInstance().getThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    onGetFileSize(total);
                }
            });
            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                OkHttpUtils.getInstance().getThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        inProgress(finalSum, total);
                    }
                });
            }
            fos.flush();
            return file;
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException ignored) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException ignored) {
            }
        }
    }
}
