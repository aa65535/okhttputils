package utils.okhttp.callback;

import java.io.IOException;

import okhttp3.Response;

@SuppressWarnings("unused")
public abstract class StringCallback extends Callback<String> {
    @Override
    public String parseNetworkResponse(Response response) throws IOException {
        //noinspection ConstantConditions
        return response.body().string();
    }
}
