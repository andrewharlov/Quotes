package com.harlov.quotes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BinomoAPI {
    @GET("time?locale=ru")
    Call<ServerTimeData> getServerTime();

    @GET("quotes/values?locale=ru&asset=BIN")
    Call<QuotesData> getQuotes(@Query("from_datetime") String fromDatetime);
}
