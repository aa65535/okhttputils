package utils.okhttp.request;

public class HeadBuilder extends GetBuilder {
    public HeadBuilder() {
    }

    HeadBuilder(GetRequest request) {
        super(request);
    }

    @Override
    public HeadRequest build() {
        this.url = getUrl();
        return new HeadRequest(this);
    }
}
