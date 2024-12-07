/*
 * Created by FakeException on 2024/12/07 12:03
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2024/12/07 12:03
 */

package com.robuxearny.official.activities.impl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.enums.Gender;
import com.robuxearny.official.survey.AppsPrizeSurvey;
import com.robuxearny.official.utils.SharedPrefsHelper;

import java.io.IOException;

public class UserInfoActivity extends BaseActivity {

    private RadioGroup radioGroupGender;

    private NumberPicker numberPickerAge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        MaterialToolbar tbToolBar = findViewById(R.id.user_info_tb_toolbar);
        tbToolBar.setNavigationOnClickListener(v -> finish());

        SharedPrefsHelper prefsHelper = new SharedPrefsHelper(this);
        numberPickerAge = findViewById(R.id.number_picker_age);
        numberPickerAge.setMinValue(3); // Set minimum age
        numberPickerAge.setMaxValue(100); // Set maximum age
        numberPickerAge.setValue(18); // Set default age

        radioGroupGender = findViewById(R.id.radio_group_gender);

        Button buttonSubmit = findViewById(R.id.button_submit);

        buttonSubmit.setOnClickListener(v -> {
            int age = numberPickerAge.getValue(); // Get selected age
            int genderId = radioGroupGender.getCheckedRadioButtonId();
            Gender gender = null;

            if (genderId == R.id.radio_button_male) {
                gender = Gender.MALE;
            } else if (genderId == R.id.radio_button_female) {
                gender = Gender.FEMALE;
            } else if (genderId == R.id.radio_button_other) {
                gender = Gender.OTHER;
            }

            if (gender != null) { // Check if gender is selected
                prefsHelper.setAge(age);
                prefsHelper.setGender(gender);
                prefsHelper.setUserInfo(true);
                prefsHelper.saveUserInfoToDB();

                FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser(); // Get the user's ID
                if (userId != null) {
                    try {
                        new AppsPrizeSurvey(this, userId.getUid());
                    } catch (IOException | GooglePlayServicesRepairableException |
                             GooglePlayServicesNotAvailableException e) {
                        throw new RuntimeException(e);
                    }
                }

                finish();
                startActivity(new Intent(this, OfferwallsActivity.class));
            } else {
                // Display an error message to the user
                Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            }
        });
    }
}