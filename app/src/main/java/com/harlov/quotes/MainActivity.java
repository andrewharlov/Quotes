package com.harlov.quotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private BinomoAPI binomoAPI;
    private LineChart lineChart;
    private ArrayList<BIN> BINs;
    private SwipeRefreshLayout quotesRefreshLayout;
    private int multipleOf = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChart = (LineChart) findViewById(R.id.line_chart);
        lineChart.setDescription(" ");
        lineChart.setNoDataText(" ");
        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleXEnabled(true);
        lineChart.setScaleYEnabled(false);
        lineChart.getLegend().setEnabled(false);

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_END_POINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        binomoAPI = retrofit.create(BinomoAPI.class);
        quotesRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.quotesRefreshLayout);
        quotesRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadServerTime();
            }
        });

        if (savedInstanceState == null){
            quotesRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    quotesRefreshLayout.setRefreshing(true);
                }
            });
            loadServerTime();
        }
        else {
            if (savedInstanceState.containsKey("BINs")
                    && savedInstanceState.containsKey("multipleOf")){
                BINs = savedInstanceState.getParcelableArrayList("BINs");
                multipleOf = savedInstanceState.getInt("multipleOf");

                if (BINs != null){
                    ListIterator<BIN> iterator = BINs.listIterator();
                    List<Entry> entries = new ArrayList<Entry>();

                    while (iterator.hasNext()){
                        int index = iterator.nextIndex();
                        BIN item = iterator.next();
                        if (index % multipleOf == 0) {
                            Float rate = Float.parseFloat(item.getRate());
                            entries.add(new Entry(index, rate));
                        }
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "Label");
                    dataSet.setHighlightEnabled(false);
                    LineData lineData = new LineData(dataSet);
                    lineChart.setData(lineData);
                    lineChart.animateX(3000);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_all:
                multipleOf = 1;
                break;
            case R.id.action_every_fifth:
                multipleOf = 5;
                break;
            case R.id.action_every_twenty_fifth:
                multipleOf = 25;
                break;
            case R.id.action_every_hundredth:
                multipleOf = 100;
                break;
        }

        if (BINs != null){
            ListIterator<BIN> iterator = BINs.listIterator();
            List<Entry> entries = new ArrayList<Entry>();
            while (iterator.hasNext()){
                int index = iterator.nextIndex();
                BIN binItem = iterator.next();
                if (index % multipleOf == 0) {
                    Float rate = Float.parseFloat(binItem.getRate());
                    entries.add(new Entry(index, rate));
                }
            }

            LineDataSet dataSet = new LineDataSet(entries, "Label");
            dataSet.setHighlightEnabled(false);
            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChart.animateX(3000);
        }

        return super.onOptionsItemSelected(item);
    }


    public void loadServerTime(){
        Call<ServerTimeData> serverTimeDataCall = binomoAPI.getServerTime();

        serverTimeDataCall.enqueue(new Callback<ServerTimeData>() {
            @Override
            public void onResponse(Call<ServerTimeData> call, Response<ServerTimeData> response) {
                ServerTimeData serverTimeData = response.body();
                ServerTime serverTime = serverTimeData.getData();
                long timestamp = serverTime.getTime();
                loadQuotes(timestamp);
            }

            @Override
            public void onFailure(Call<ServerTimeData> call, Throwable t) {
                loadCachedChartData();
                quotesRefreshLayout.setRefreshing(false);
                Snackbar.make(findViewById(android.R.id.content), R.string.error_message, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void loadQuotes(long timestamp){
        timestamp = timestamp - 1800;
        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new java.util.Date(timestamp * 1000));

        Call<QuotesData> quotesDataCall = binomoAPI.getQuotes(date);
        quotesDataCall.enqueue(new Callback<QuotesData>() {
            @Override
            public void onResponse(Call<QuotesData> call, Response<QuotesData> response) {
                QuotesData quotesData = response.body();
                Quotes quotes = quotesData.getData();
                BINs = quotes.getBIN();

                ListIterator<BIN> iterator = BINs.listIterator();
                List<Entry> entries = new ArrayList<Entry>();
                while (iterator.hasNext()) {
                    int index = iterator.nextIndex();
                    BIN item = iterator.next();
                    if (index % multipleOf == 0) {
                        Float rate = Float.parseFloat(item.getRate());
                        entries.add(new Entry(index, rate));
                    }
                }

                LineDataSet dataSet = new LineDataSet(entries, "Label");
                dataSet.setHighlightEnabled(false);
                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);
                lineChart.animateX(3000);
                quotesRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<QuotesData> call, Throwable t) {
                loadCachedChartData();
                quotesRefreshLayout.setRefreshing(false);
                Snackbar.make(findViewById(android.R.id.content), R.string.error_message, Snackbar.LENGTH_LONG).show();
            }
        });
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("BINs", BINs);
        outState.putInt("multipleOf", multipleOf);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveChartData();
    }

    public void loadCachedChartData(){
        final String PREFS_NAME = Constants.CHART_DATA_PREF;
        final String PREF_BINS_DATA_KEY = Constants.BINS_DATA_KEY;
        final String DEFAULT_VALUE = "";

        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String binsJson = sharedPref.getString(PREF_BINS_DATA_KEY, DEFAULT_VALUE);

        quotesRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                quotesRefreshLayout.setRefreshing(false);
            }
        });

        if (!binsJson.equals("")){
            Gson gson = new Gson();
            Type type = new TypeToken<List<BIN>>(){}.getType();
            BINs = gson.fromJson(binsJson, type);

            if (BINs != null){
                ListIterator<BIN> iterator = BINs.listIterator();
                List<Entry> entries = new ArrayList<Entry>();

                while (iterator.hasNext()){
                    int index = iterator.nextIndex();
                    BIN item = iterator.next();
                    if (index % multipleOf == 0) {
                        Float rate = Float.parseFloat(item.getRate());
                        entries.add(new Entry(index, rate));
                    }
                }

                LineDataSet dataSet = new LineDataSet(entries, "Label");
                dataSet.setHighlightEnabled(false);
                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);
                lineChart.animateX(3000);
            }
        }
    }

    public void saveChartData(){

        final String PREFS_NAME = Constants.CHART_DATA_PREF;
        final String PREF_BINS_DATA_KEY = Constants.BINS_DATA_KEY;

        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Gson gson = new Gson();
        String binsJson = gson.toJson(BINs);
        editor.putString(PREF_BINS_DATA_KEY, binsJson);
        editor.apply();
    }
}
