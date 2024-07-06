package com.robuxearny.official.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.robuxearny.official.R;
import com.robuxearny.official.callbacks.FAQCallback;
import com.robuxearny.official.callbacks.PackageCallback;
import com.robuxearny.official.models.FAQItem;
import com.robuxearny.official.models.Package;
import com.robuxearny.official.network.BackendService;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackendUtils {

    public static void fetchPackages(Context context, PackageCallback callback) {
        String apiUrl = context.getString(R.string.api_url);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService backendService = retrofit.create(BackendService.class);
        Call<List<Package>> call = backendService.getPackages();
        call.enqueue(new Callback<List<Package>>() {
            @Override
            public void onResponse(Call<List<Package>> call, Response<List<Package>> response) {
                if (response.isSuccessful()) {
                    List<Package> packages = response.body();
                    callback.onPackagesLoaded(packages);
                } else {
                    Log.e("RedeemActivity", "Error fetching packages: "+ response.code());
                    callback.onError("Error fetching packages: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Package>> call, Throwable t) {
                Log.e("RedeemActivity", "Network error fetching packages: " + t.getMessage());
                callback.onError("Network error fetching packages: " + t.getMessage());
            }
        });
    }

    public static void fetchFAQs(Context context, FAQCallback callback) {
        String apiUrl = context.getString(R.string.api_url);
        String locale = Locale.getDefault().getLanguage();

        fetchFAQsForLocale(apiUrl, locale, callback);
    }

    private static void fetchFAQsForLocale(String apiUrl, String locale, FAQCallback callback) {
        String jsonUrl = apiUrl + "faqs_" + locale + ".json";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService backendService = retrofit.create(BackendService.class);
        Call<List<FAQItem>> call = backendService.getFAQs(jsonUrl);
        call.enqueue(new Callback<List<FAQItem>>() {
            @Override
            public void onResponse(Call<List<FAQItem>> call, Response<List<FAQItem>> response) {
                if (response.isSuccessful()) {
                    List<FAQItem> faqs = response.body();
                    callback.onFAQsLoaded(faqs);
                } else {
                    // If the requested language fails, try English
                    if (!locale.equals("en")) {
                        fetchFAQsForLocale(apiUrl, "en", callback);
                    } else {
                        Log.e("faq", String.valueOf(response.code()));
                        callback.onError("Error fetching FAQs: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<FAQItem>> call, Throwable t) {
                // If the network request fails, try English
                if (!locale.equals("en")) {
                    fetchFAQsForLocale(apiUrl, "en", callback);
                } else {
                    Log.e("faq", t.getMessage());
                    callback.onError("Network error fetching FAQs: " + t.getMessage());
                }
            }
        });
    }
}