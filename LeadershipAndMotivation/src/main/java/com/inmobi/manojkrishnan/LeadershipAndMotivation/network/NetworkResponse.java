package com.inmobi.manojkrishnan.LeadershipAndMotivation.network;

import java.util.List;
import java.util.Map;

/**
 * Created by amit.prabhudesai on 03/12/14.
 */
public class NetworkResponse {


    private String mResponse;
    private NetworkError mError;
    private Map<String, List<String>> mHeaders;





    public boolean isError() {
        return (mError != null);
    }

    public String getResponse() {
        return mResponse;
    }

    public void setResponse(String response) {
        this.mResponse = response;
    }

    public Map<String, List<String>> getHeaders() {
        return mHeaders;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.mHeaders = headers;
    }

    public NetworkError getError() {
        return mError;
    }

    public void setError(NetworkError error) {
        this.mError = error;
    }
}
