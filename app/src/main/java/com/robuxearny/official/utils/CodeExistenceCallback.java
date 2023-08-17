/*
 * Created by FakeException on 8/17/23, 12:26 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/17/23, 12:26 PM
 */

package com.robuxearny.official.utils;

public interface CodeExistenceCallback {
    void onCodeExistenceChecked(boolean exists, String referrer);
}