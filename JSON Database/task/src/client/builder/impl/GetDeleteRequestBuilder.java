package client.builder.impl;

import client.builder.IRequestBuilder;
import client.model.GetDeleteRequest;
import client.model.Input;
import client.model.Request;
import com.google.gson.Gson;

import java.util.Set;

public class GetDeleteRequestBuilder implements IRequestBuilder {
    @Override
    public Set<String> getSupportedTitles() {
        return Set.of("get", "delete");
    }

    @Override
    public Request buildRequest(Input parameters) {
        return new GetDeleteRequest(parameters.getType(), parameters.getKey());
    }

    @Override
    public GetDeleteRequest buildRequest(String jsonRequest) {
        Gson gson = new Gson();
        return gson.fromJson(jsonRequest, GetDeleteRequest.class);
    }
}
