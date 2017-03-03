package com.inmobi.manojkrishnan.LeadershipAndMotivation.network;

import java.net.HttpURLConnection;

/**
 * Created by nitin.singh on 02/12/14.
 */
public class NetworkError {

    public enum ErrorCode {

        NETWORK_UNAVAILABLE_ERROR(0),
        UNKNOWN_ERROR(-1),
        NETWORK_IO_ERROR(-2),
        OUT_OF_MEMORY_ERROR(-3),
        INVALID_ENCRYPTED_RESPONSE_RECEIVED(-4),
        RESPONSE_EXCEEDS_SPECIFIED_SIZE_LIMIT(-5),
        GZIP_DECOMPRESSION_FAILED(-6),
        HTTP_NO_CONTENT(HttpURLConnection.HTTP_NO_CONTENT),
        HTTP_NOT_MODIFIED(HttpURLConnection.HTTP_NOT_MODIFIED),
        HTTP_BAD_REQUEST(HttpURLConnection.HTTP_BAD_REQUEST),
        HTTP_SEE_OTHER(HttpURLConnection.HTTP_SEE_OTHER),
        HTTP_SERVER_NOT_FOUND(HttpURLConnection.HTTP_NOT_FOUND),
        HTTP_MOVED_TEMP(HttpURLConnection.HTTP_MOVED_TEMP),
        HTTP_INTERNAL_SERVER_ERROR(HttpURLConnection.HTTP_INTERNAL_ERROR),
        HTTP_NOT_IMPLEMENTED(HttpURLConnection.HTTP_NOT_IMPLEMENTED),
        HTTP_BAD_GATEWAY(HttpURLConnection.HTTP_BAD_GATEWAY),
        HTTP_SERVER_NOT_AVAILABLE(HttpURLConnection.HTTP_UNAVAILABLE),
        HTTP_GATEWAY_TIMEOUT(HttpURLConnection.HTTP_GATEWAY_TIMEOUT),
        HTTP_VERSION_NOT_SUPPORTED(HttpURLConnection.HTTP_VERSION);

        private int mValue;

        private ErrorCode(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }

        public static ErrorCode fromValue(int value) {
            for (ErrorCode errorCode : ErrorCode.values()) {
                if (errorCode.mValue == value) {
                    return errorCode;
                }
            }
            return null;
        }
    }

    private ErrorCode mErrorCode;
    private String mErrorMessage;

    public NetworkError(ErrorCode errorCode, String errorMessage) {
        mErrorCode = errorCode;
        mErrorMessage = errorMessage;
    }

    public ErrorCode getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        mErrorCode = errorCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        mErrorMessage = errorMessage;
    }

}
