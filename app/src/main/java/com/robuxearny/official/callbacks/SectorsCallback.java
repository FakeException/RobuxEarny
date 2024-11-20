/*
 * Created by FakeException on 2024/11/20 19:25
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2024/11/20 19:25
 */

package com.robuxearny.official.callbacks;

import com.robuxearny.official.models.Sector;

import java.util.List;

public interface SectorsCallback {

    void onSectorsRetrieved(List<Sector> sectors);
    void onError(String errorMessage);
}
