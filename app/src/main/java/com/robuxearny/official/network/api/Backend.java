package com.robuxearny.official.network.api;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Backend {
    private static final String BASE_URL = "https://7da5-151-73-146-110.ngrok-free.app/accounts/";
    public static final String ACCESS = BASE_URL + "access";
    public static final String CHECK_TOKEN = BASE_URL + "checkToken";

    public static String access(String username, String password) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<String> future = new CompletableFuture<>();

        executor.execute(() -> {
            OkHttpClient client = new OkHttpClient();

            FormBody postBody = new FormBody.Builder()
                    .addEncoded("name", username)
                    .addEncoded("password", password)
                    .build();

            Request request = new Request.Builder()
                    .post(postBody)
                    .url(ACCESS)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    future.complete(response.body().string());
                } else {
                    future.complete(null);
                }
            } catch (IOException e) {
                future.completeExceptionally(e);
            }
        });

        executor.shutdown();
        return future.get();
    }

    public static String tokenValid(String token) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<String> future = new CompletableFuture<>();

        executor.execute(() -> {
            OkHttpClient client = new OkHttpClient();

            FormBody postBody = new FormBody.Builder()
                    .addEncoded("token", token)
                    .build();

            Request request = new Request.Builder()
                    .post(postBody)
                    .url(CHECK_TOKEN)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    future.complete(response.body().string());
                } else {
                    future.complete(null);
                }
            } catch (IOException e) {
                future.completeExceptionally(e);
            }
        });

        executor.shutdown();
        return future.get();
    }
}
