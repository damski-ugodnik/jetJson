package client.model;

public class GetDeleteRequest extends Request{
    private final String key;

    public GetDeleteRequest(String type, String key) {
        super(type);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
