package client.model;

public class SetTextRequest extends GetDeleteRequest {
    private final String value;

    public SetTextRequest(String type, String key, String value) {
        super(type, key);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
