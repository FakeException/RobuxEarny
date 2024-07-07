/*
 * Created by Fake on 7/6/24, 5:26 PM
 * Copyright (c) 2024. All rights reserved.
 * Last modified 7/6/24, 5:26 PM
 */

package com.robuxearny.official.models;

import com.google.gson.annotations.SerializedName;

public class FAQItem {

    @SerializedName("question")
    public String question;

    @SerializedName("answer")
    public String answer;
}