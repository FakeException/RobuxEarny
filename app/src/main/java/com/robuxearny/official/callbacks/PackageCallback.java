/*
 * Created by Fake on 7/6/24, 6:50 PM
 * Copyright (c) 2024. All rights reserved.
 * Last modified 7/6/24, 6:50 PM
 */

package com.robuxearny.official.callbacks;

import com.robuxearny.official.models.Package;

import java.util.List;

public interface PackageCallback {
    void onPackagesLoaded(List<Package> packages);
    void onError(String errorMessage);
}
