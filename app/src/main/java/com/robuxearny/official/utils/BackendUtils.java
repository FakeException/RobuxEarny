package com.robuxearny.official.utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.callbacks.FAQCallback;
import com.robuxearny.official.callbacks.MoneyRetrievedCallback;
import com.robuxearny.official.callbacks.OfferwallsCallback;
import com.robuxearny.official.callbacks.PackageCallback;
import com.robuxearny.official.callbacks.SectorsCallback;
import com.robuxearny.official.models.FAQItem;
import com.robuxearny.official.models.OfferWall;
import com.robuxearny.official.models.Package;
import com.robuxearny.official.models.Sector;
import com.robuxearny.official.network.BackendService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackendUtils {

    private final String apiUrl;
    private final Retrofit baseRetrofit;

    public BackendUtils(Context context) {
        this.apiUrl = context.getString(R.string.api_url);

        this.baseRetrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void fetchSectors(SectorsCallback callback) {

        BackendService backendService = baseRetrofit.create(BackendService.class);
        Call<List<Sector>> call = backendService.getSectors();
        call.enqueue(new Callback<List<Sector>>() {
            @Override
            public void onResponse(Call<List<Sector>> call, Response<List<Sector>> response) {
                if (response.isSuccessful()) {
                    List<Sector> sectors = response.body();
                    callback.onSectorsRetrieved(sectors);
                } else {
                    Log.e("SpinTheWheelActivity", "Error fetching sectors: "+ response.code());
                    callback.onError("Error fetching sectors: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Sector>> call, Throwable t) {
                Log.e("SpinTheWheelActivity", "Network error fetching sectors: " + t.getMessage());
                callback.onError("Network error fetching sectors: " + t.getMessage());
            }
        });
    }

    public void fetchOfferwalls(OfferwallsCallback callback) {

        BackendService backendService = baseRetrofit.create(BackendService.class);
        Call<List<OfferWall>> call = backendService.getOfferWalls();
        call.enqueue(new Callback<List<OfferWall>>() {
            @Override
            public void onResponse(Call<List<OfferWall>> call, Response<List<OfferWall>> response) {
                if (response.isSuccessful()) {
                    List<OfferWall> offerWalls = response.body();
                    callback.onOfferwallsRetrieved(offerWalls);
                } else {
                    Log.e("SpinTheWheelActivity", "Error fetching sectors: "+ response.code());
                    callback.onError("Error fetching sectors: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<OfferWall>> call, Throwable t) {
                Log.e("SpinTheWheelActivity", "Network error fetching offerwalls: " + t.getMessage());
                callback.onError("Network error fetching offerwalls: " + t.getMessage());
            }
        });
    }

    public void fetchPackages(PackageCallback callback) {

        BackendService backendService = baseRetrofit.create(BackendService.class);
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

    public void fetchFAQs(FAQCallback callback) {
        String locale = Locale.getDefault().getLanguage();

        fetchFAQsForLocale(locale, callback);
    }

    private void fetchFAQsForLocale(String locale, FAQCallback callback) {
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
                        fetchFAQsForLocale("en", callback);
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
                    fetchFAQsForLocale("en", callback);
                } else {
                    Log.e("faq", t.getMessage());
                    callback.onError("Network error fetching FAQs: " + t.getMessage());
                }
            }
        });
    }

    public static void retrieveMoney(String uid, BaseActivity context, MoneyRetrievedCallback callback) {

        if (uid != null) {

            Log.d("Coins", "Current UID: " + uid);

            context.getDb().collection("users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d("Coins", "Document Data: " + document);

                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        Log.d("Coins", "Document Data: " + data);

                        Long coinsLong = document.getLong("coins");
                        if (coinsLong != null) {
                            long coins = coinsLong;
                            context.getPrefsEditor().putInt("coins", (int) coins).apply();
                            callback.onMoneyLoaded();
                        }
                    }
                }
            });
        }
    }
}