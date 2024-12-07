/*
 * Created by FakeException on 2024/12/07 11:19
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2023/10/09 19:51
 */

package com.robuxearny.official.survey;

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
                "vubKNg9JfCUeAWbvgoEO5NY0PaL0g9qt",
                style)
                .build();

        this.cpxResearch = CPXResearch.Companion.init(config);
    }

    @NonNull
    public CPXResearch getCpxResearch() {
        return cpxResearch;
    }
}