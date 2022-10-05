package client.builder.impl;

import client.builder.IRequestBuilder;
import client.model.Input;
import client.model.Request;
import client.model.SetTextRequest;
import com.google.gson.Gson;

import java.util.Set;

public class SetTextRequestBuilder implements IRequestBuilder {
    @Override
    public Set<String> getSupportedTitles() {
        return Set.of("set");
    }

    @Override
    public Request buildRequest(Input parameters) {
        return new SetTextRequest(parameters.getType(), parameters.getKey(), parameters.getValue());
    }

    @Override
    public SetTextRequest buildRequest(String jsonRequest) {
        Gson gson = new Gson();
        return gson.fromJson(jsonRequest, SetTextRequest.class);
    }
}
