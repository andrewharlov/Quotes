package com.harlov.quotes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ServerTimeData {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("data")
    @Expose
    private ServerTime data;

    @SerializedName("errors")
    @Expose
    private ArrayList<ServerError> errors;

    public boolean isSuccess() {
        return success;
    }

    public ServerTime getData() {
        return data;
    }

    public ArrayList<ServerError> getErrors() {
        return errors;
    }
}
