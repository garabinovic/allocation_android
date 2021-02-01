package com.andrijag.allocation.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.andrijag.allocation.R;
import com.apollographql.apollo.ApolloClient;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Storage {
    public void setMyEvents(List<MyEvent> mMyEvents) {
        this.mMyEvents = mMyEvents;
    }

    private List<MyEvent> mMyEvents;
    public String token = "";
    private static Storage sStorage;
    public static Storage get(Context context) {
        if (sStorage == null) {
            sStorage = new Storage(context);
        }
        return sStorage;
    }
    private Storage(Context context) {
        mMyEvents = new ArrayList<>();
    }
    public List<MyEvent> getMyEvents() {
        return mMyEvents;
    }

    static public ApolloClient provideApolloClient(final Context context) {

        final SharedPreferences pref = context.getSharedPreferences("Allocation", 0); // 0 - for private mode
        return ApolloClient.builder()
                .serverUrl("http://45.32.157.171:9090/graphql")
                .okHttpClient(new OkHttpClient().newBuilder()
                        .addInterceptor(
                                new Interceptor() {
                                    @NotNull
                                    @Override
                                    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
                                        Request original = chain.request();

                                        Request request = original.newBuilder()
                                                .header("Authorization", pref.getString("token",""))
                                                .method(original.method(), original.body())
                                                .build();

                                        return chain.proceed(request);
                                    }
                                })
                        .build())
                .build();
    }



    static public String convertDateTimeFormat(String inputPattern, String outputPattern, String dateTimeStamp){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        try {
            Date date = inputFormat.parse(dateTimeStamp);
            assert date != null;
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


}

