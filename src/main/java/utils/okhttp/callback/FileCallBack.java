package utils.okhttp.callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.Response;
import okhttp3.internal.Util;
import utils.okhttp.OkHttpUtils;

@SuppressWarnings("unused")
public abstract class FileCallBack extends Callback<File> {
    private static final int BUFFER_SIZE = 8192;

    private File destFile;
    private int bufferSize;

    /**
     * @param destFile 目标文件
     */
    public FileCallBack(File destFile) {
        this(destFile, BUFFER_SIZE);
    }

    /**
     * @param destFileDir 文件保存的目录
     * @param fileName    文件名
     */
    public FileCallBack(String destFileDir, String fileName) {
        this(destFileDir, fileName, BUFFER_SIZE);
    }

    /**
     * @param destFileDir 文件保存的目录
     * @param fileName    文件名
     */
    public FileCallBack(File destFileDir, String fileName) {
        this(destFileDir, fileName, BUFFER_SIZE);
    }

    /**
     * @param destFile   目标文件
     * @param bufferSize 缓存区大小
     */
    public FileCallBack(File destFile, int bufferSize) {
        this.destFile = destFile;
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        this.bufferSize = bufferSize;
    }

    /**
     * @param destFileDir 文件保存的目录
     * @param fileName    文件名
     * @param bufferSize  缓存区大小
     */
    public FileCallBack(String destFileDir, String fileName, int bufferSize) {
        this(new File(destFileDir, fileName), bufferSize);
    }

    /**
     * @param destFileDir 文件保存的目录
     * @param fileName    文件名
     * @param bufferSize  缓存区大小
     */
    public FileCallBack(File destFileDir, String fileName, int bufferSize) {
        this(new File(destFileDir, fileName), bufferSize);
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
        FileOutputStream fos = null;
        byte[] buf = new byte[bufferSize];
        int len;
        try {
            //noinspection ConstantConditions
            final long total = response.body().contentLength();
            long sum = 0;
            OkHttpUtils.getInstance().getThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    onGetFileSize(total);
                }
            });
            destFile.getParentFile().mkdirs();
            is = response.body().byteStream();
            fos = new FileOutputStream(destFile);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                final long finalSum = (sum += len);
                OkHttpUtils.getInstance().getThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        inProgress(finalSum, total);
                    }
                });
            }
            fos.flush();
            return destFile;
        } finally {
            Util.closeQuietly(is);
            Util.closeQuietly(fos);
        }
    }
}
