package com.harlov.quotes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerError {
    @SerializedName("field")
    @Expose
    private String field;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("code")
    @Expose
    private String code;

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
