package com.andrijag.allocation.models;

import android.content.Context;

import com.apollographql.apollo.ApolloClient;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class Storage {

    private List<MyEvent> mMyEvents;
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

    static public ApolloClient provideApolloClient() {
        return ApolloClient.builder()
                .serverUrl("http://45.32.157.171:9090/graphql")
                .okHttpClient(new OkHttpClient().newBuilder().build())
                .build();
    }
}