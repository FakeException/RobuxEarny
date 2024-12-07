/*
 * Created by FakeException on 2024/11/20 19:25
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2024/11/20 19:25
 */

package com.robuxearny.official.callbacks;

import com.robuxearny.official.models.OfferWall;

import java.util.List;

public interface OfferwallsCallback {

    void onOfferwallsRetrieved(List<OfferWall> offerWalls);
    void onError(String errorMessage);
}
