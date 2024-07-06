/*
 * Created by Fake on 7/6/24, 5:22 PM
 * Copyright (c) 2024. All rights reserved.
 * Last modified 7/6/24, 5:22 PM
 */

package com.robuxearny.official.network;

import com.robuxearny.official.models.FAQItem;
import com.robuxearny.official.models.Package;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface BackendService {

    @GET
    Call<List<FAQItem>> getFAQs(@Url String url);

    @GET("packages.json")
    Call<List<Package>> getPackages();
}
