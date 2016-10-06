package com.harlov.quotes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerTime {
    @SerializedName("time")
    @Expose
    private long time;

    public long getTime() {
        return time;
    }
}
