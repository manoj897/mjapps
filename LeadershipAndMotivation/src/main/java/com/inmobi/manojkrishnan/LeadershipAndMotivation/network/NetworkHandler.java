package com.inmobi.manojkrishnan.LeadershipAndMotivation.network;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by manoj.krishnan on 6/1/16.
 */
public class NetworkHandler {
    private HttpURLConnection mHttpUrlConnection;

    public NetworkResponse connect(String url) {
        NetworkResponse response = null;
        try {
            mHttpUrlConnection = setupConnection(url);
            response = retrieveResponse();
            Log.d("test", "Response is" + response.getResponse());
        } catch (IOException e) {
            response = new NetworkResponse();
            response.setError(new NetworkError(NetworkError.ErrorCode.NETWORK_IO_ERROR, e.getLocalizedMessage()));
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            response = new NetworkResponse();
            response.setError(new NetworkError(NetworkError.ErrorCode.HTTP_BAD_REQUEST, "The URL is malformed:" + NetworkError.ErrorCode.HTTP_BAD_REQUEST.toString()));
            e.printStackTrace();
        } catch (SecurityException e) {
            response = new NetworkResponse();
            response.setError(new NetworkError(NetworkError.ErrorCode.UNKNOWN_ERROR, e.getLocalizedMessage()));
            e.printStackTrace();


        }
        return response;
    }
    private HttpURLConnection setupConnection(String url) throws IOException {
        URL serverUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
        return connection;
    }
    private NetworkResponse retrieveResponse() {
        NetworkResponse response = new NetworkResponse();

        try {
            int responseCode = mHttpUrlConnection.getResponseCode();
            Log.d("test", "responseC0de" + responseCode);
            InputStream inputStream = null;
            try {
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    inputStream = mHttpUrlConnection.getInputStream();
                    byte[] responseBytes = NetworkUtils.toByteArray(inputStream);
                    //Response has no content.
                    if(0 == responseBytes.length) {
                        Log.d("test", "length is " + 0);
                        response.setResponse("");
                    } else {

                        //Check if decryption is successful before de-compression

                        if(null != responseBytes) {
                            Log.d("test", "response is valid");
                            response.setResponse(new String(responseBytes, "UTF-8"));
                        }
                    }
                    response.setHeaders(mHttpUrlConnection.getHeaderFields());

                } else {
                    NetworkError.ErrorCode errorCode = NetworkError.ErrorCode.fromValue(responseCode);
                    if (errorCode == null) {
                        errorCode = NetworkError.ErrorCode.UNKNOWN_ERROR;
                    }
                    response.setError(new NetworkError(errorCode, "HTTP:" + responseCode));
                    response.setHeaders(mHttpUrlConnection.getHeaderFields());
                }
            } finally {
                NetworkUtils.closeSilently(inputStream);
                mHttpUrlConnection.disconnect();
            }
        } catch (SocketTimeoutException e) {
            response.setError(new NetworkError(NetworkError.ErrorCode.HTTP_GATEWAY_TIMEOUT, NetworkError.ErrorCode.HTTP_GATEWAY_TIMEOUT.toString()));
            e.printStackTrace();
        } catch (IOException e) {
            response.setError(new NetworkError(NetworkError.ErrorCode.NETWORK_IO_ERROR, NetworkError.ErrorCode.NETWORK_IO_ERROR.toString()));
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            response.setError(new NetworkError(NetworkError.ErrorCode.OUT_OF_MEMORY_ERROR, NetworkError.ErrorCode.OUT_OF_MEMORY_ERROR.toString()));
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            // In versions of the OkHttp library < 1.5.4, closing a connection from multiple threads
            // causes an ArrayIndexOutOfBoundsException.
            // See here: https://github.com/square/okhttp/issues/658
            // To avoid a crash on older versions of OkHttp, we will catch the Exception here
            response.setError(new NetworkError(NetworkError.ErrorCode.UNKNOWN_ERROR, NetworkError.ErrorCode.UNKNOWN_ERROR.toString()));
            e.printStackTrace();


        }
        return response;
    }
}
