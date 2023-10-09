/*
 * Created by FakeException on 10/9/23, 11:29 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 10/9/23, 11:29 AM
 */

package com.robuxearny.official.utils;

import androidx.annotation.NonNull;

import com.makeopinion.cpxresearchlib.CPXResearch;
import com.makeopinion.cpxresearchlib.models.CPXConfiguration;
import com.makeopinion.cpxresearchlib.models.CPXConfigurationBuilder;
import com.makeopinion.cpxresearchlib.models.CPXStyleConfiguration;
import com.makeopinion.cpxresearchlib.models.SurveyPosition;

public class CPX {

    private final CPXResearch cpxResearch;

    public CPX(String id) {
        CPXStyleConfiguration style = new CPXStyleConfiguration(SurveyPosition.CornerBottomRight,
                "Earn Robux faster!",
                16,
                "#ffffff",
                "#CBC3E3",
                true);

        CPXConfiguration config = new CPXConfigurationBuilder("20130",
                id,
                "secrettttttt",
                style)
                .build();

        this.cpxResearch = CPXResearch.Companion.init(config);
    }

    @NonNull
    public CPXResearch getCpxResearch() {
        return cpxResearch;
    }
}