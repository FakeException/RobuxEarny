/*
 * Created by Fake on 7/6/24, 4:59 PM
 * Copyright (c) 2024. All rights reserved.
 * Last modified 8/17/23, 12:44 PM
 */

package com.robuxearny.official.callbacks;

public interface CodeExistenceCallback {
    void onCodeExistenceChecked(boolean exists, String referrer);
}