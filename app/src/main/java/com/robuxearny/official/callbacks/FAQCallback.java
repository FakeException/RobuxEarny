/*
 * Created by Fake on 7/6/24, 5:35 PM
 * Copyright (c) 2024. All rights reserved.
 * Last modified 7/6/24, 5:35 PM
 */

package com.robuxearny.official.callbacks;

import com.robuxearny.official.models.FAQItem;

import java.util.List;

public interface FAQCallback {
    void onFAQsLoaded(List<FAQItem> faqs);
    void onError(String errorMessage);
}