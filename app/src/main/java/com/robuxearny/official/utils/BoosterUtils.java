/*
 * Created by Fake on 6/30/24, 1:48 PM
 * Copyright (c) 2024. All rights reserved.
 * Last modified 6/30/24, 1:48 PM
 */

package com.robuxearny.official.utils;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BoosterUtils {

    public static void enableBoost4x(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    userRef.update("has4xBooster", true)
                            .addOnSuccessListener(obj -> Log.d("Booster", "4x Booster purchased"))
                            .addOnFailureListener(exc -> Log.d("Booster", exc.getMessage()));
                }
            }

        });
    }

    public static void enableBoost10x(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    userRef.update("has10xBooster", true)
                            .addOnSuccessListener(obj -> Log.d("Booster", "10x Booster purchased"))
                            .addOnFailureListener(exc -> Log.d("Booster", exc.getMessage()));
                }
            }

        });
    }

    public static void checkHas4xBooster(String uid, Callback<Boolean> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    callback.onResult(Boolean.TRUE.equals(document.getBoolean("has4xBooster")));
                } else {
                    callback.onResult(false);
                }
            } else {
                Log.e("BoosterCheck", "Error checking booster: " + task.getException());
                callback.onResult(false);
            }
        });
    }

    public static void checkHas10xBooster(String uid, Callback<Boolean> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    callback.onResult(Boolean.TRUE.equals(document.getBoolean("has10xBooster")));
                } else {
                    callback.onResult(false);
                }
            } else {
                Log.e("BoosterCheck", "Error checking booster: " + task.getException());
                callback.onResult(false);
            }
        });
    }

    public interface Callback<T> {
        void onResult(T result);
    }
}
