package server.model;

public class SuccessfulResponse extends Response {
    private String value;

    public SuccessfulResponse(String response, String value) {
        super(response);
        this.value = value;
    }
}
