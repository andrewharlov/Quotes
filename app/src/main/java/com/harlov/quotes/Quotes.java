package com.harlov.quotes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Quotes {
    @SerializedName("BIN")
    @Expose
    private ArrayList<BIN> BIN;

    public ArrayList<BIN> getBIN() {
        return BIN;
    }
}
