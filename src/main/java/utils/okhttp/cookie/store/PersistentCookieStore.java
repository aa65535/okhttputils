package utils.okhttp.cookie.store;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Locale;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import utils.okhttp.utils.Objects;

@SuppressWarnings("unused")
public class PersistentCookieStore extends MemoryCookieStore {
    private final PersistentStore persistentStore;

    public PersistentCookieStore(PersistentStore persistentStore) {
        this.persistentStore = Objects.requireNonNull(persistentStore, "persistentStore is null");
        List<String> stringList = persistentStore.getAll();
        persistentStore.removeAll();
        if (Objects.nonNull(stringList)) {
            for (String item : stringList) {
                Cookie cookie = decodeCookie(item);
                if (Objects.nonNull(cookie) && !isExpired(cookie)) {
                    add(null, cookie);
                }
            }
        }
    }

    @Override
    public void add(HttpUrl url, Cookie cookie) {
        Objects.requireNonNull(cookie, "cookie is null");
        remove(cookie);
        if (!isExpired(cookie)) {
            cookieJar.add(cookie);
            persistentStore.add(encodeCookie(new SerializableCookie(cookie)));
        }
    }

    @Override
    public boolean removeAll() {
        if (!cookieJar.isEmpty()) {
            cookieJar.clear();
            persistentStore.removeAll();
            return true;
        }
        return false;
    }

    private String encodeCookie(SerializableCookie cookie) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
            return byteArrayToHexString(os.toByteArray());
        } catch (IOException ignored) {
        }
        return null;
    }

    private Cookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return ((SerializableCookie) objectInputStream.readObject()).getCookie();
        } catch (IOException | ClassNotFoundException ignored) {
        }
        return null;
    }

    private String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    private byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 持久化存储
     */
    public interface PersistentStore {
        /**
         * 添加编码后的 {@link Cookie} 对象字符串到持久化存储中
         *
         * @param cookieString 编码后的 {@link Cookie} 对象字符串
         */
        void add(String cookieString);

        /**
         * @return 持久化存储中所有的编码后的 {@link Cookie} 对象字符串
         */
        List<String> getAll();

        /**
         * 清空持久化存储中的内容
         */
        void removeAll();
    }
}
