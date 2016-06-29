package utils.okhttp.builder;

import utils.okhttp.OkHttpUtils;
import utils.okhttp.request.OtherRequest;
import utils.okhttp.request.RequestCall;

public class HeadBuilder extends GetBuilder {
    @Override
    public RequestCall build() {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, headers).build();
    }
}
