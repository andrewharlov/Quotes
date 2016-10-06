package com.harlov.quotes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BIN implements Parcelable{
    @SerializedName("ric")
    @Expose
    private String ric;

    @SerializedName("rate")
    @Expose
    private String rate;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    protected BIN(Parcel in) {
        ric = in.readString();
        rate = in.readString();
        created_at = in.readString();
    }

    public static final Creator<BIN> CREATOR = new Creator<BIN>() {
        @Override
        public BIN createFromParcel(Parcel in) {
            return new BIN(in);
        }

        @Override
        public BIN[] newArray(int size) {
            return new BIN[size];
        }
    };

    public String getRic() {
        return ric;
    }

    public String getRate() {
        return rate;
    }

    public String getCreated_at() {
        return created_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ric);
        dest.writeString(rate);
        dest.writeString(created_at);
    }
}
