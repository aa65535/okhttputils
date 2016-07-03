package utils.okhttp.callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;
import utils.okhttp.OkHttpUtils;

public abstract class FileCallBack extends Callback<File> {
    private File destFileDir;
    private String fileName;

    /**
     * @param destFileDir 文件保存的目录
     * @param fileName    文件名
     */
    public FileCallBack(File destFileDir, String fileName) {
        this.destFileDir = destFileDir;
        this.fileName = fileName;
    }

    /**
     * @param destFileDir 文件保存的目录
     * @param fileName    文件名
     */
    public FileCallBack(String destFileDir, String fileName) {
        this.destFileDir = new File(destFileDir);
        this.fileName = fileName;
    }

    /**
     * 当获取文件体积时调用，Android 下使用 UI 线程
     *
     * @param size 文件的体积
     */
    public abstract void onGetFileSize(long size);

    public abstract void inProgress(long progress, long size);

    @Override
    public File parseNetworkResponse(Response response) throws Exception {
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
            if (!destFileDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                destFileDir.mkdirs();
            }
            File file = new File(destFileDir, fileName);
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
