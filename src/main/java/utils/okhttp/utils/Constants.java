package utils.okhttp.utils;

import okhttp3.MediaType;

public interface Constants {
    MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");
    MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
}
