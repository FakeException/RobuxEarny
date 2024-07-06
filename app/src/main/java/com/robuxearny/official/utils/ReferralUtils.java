/*
 * Created by Fake on 7/6/24, 5:08 PM
 * Copyright (c) 2024. All rights reserved.
 * Last modified 7/6/24, 5:08 PM
 */

package com.robuxearny.official.utils;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.callbacks.ReferralCodeCallback;

public class ReferralUtils {

    public static void saveUserReferral(FirebaseFirestore db, String uid, ReferralCodeCallback callback) {
        db.collection("referralCodes")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String referralCode = documentSnapshot.getId();
                        callback.onReferralCodeRetrieved(referralCode);
                    } else {
                        // Handle the case where no referral code is found for the user
                        callback.onReferralCodeRetrieved(null);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors in fetching the referral code
                    Log.e("Referral", "Error retrieving referral code: ", e);
                    callback.onReferralCodeRetrieved(null);
                });
    }
}
