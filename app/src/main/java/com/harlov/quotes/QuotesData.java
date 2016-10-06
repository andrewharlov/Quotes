package com.harlov.quotes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class QuotesData {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("data")
    @Expose
    private Quotes data;

    @SerializedName("errors")
    @Expose
    private ArrayList<ServerError> errors;

    public boolean isSuccess() {
        return success;
    }

    public Quotes getData() {
        return data;
    }

    public ArrayList<ServerError> getErrors() {
        return errors;
    }
}
