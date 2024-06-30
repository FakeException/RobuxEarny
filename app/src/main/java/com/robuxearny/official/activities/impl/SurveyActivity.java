package com.robuxearny.official.activities.impl;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.appodeal.ads.Appodeal;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.makeopinion.cpxresearchlib.CPXResearchListener;
import com.makeopinion.cpxresearchlib.models.CPXCardConfiguration;
import com.makeopinion.cpxresearchlib.models.CPXCardStyle;
import com.makeopinion.cpxresearchlib.models.SurveyItem;
import com.makeopinion.cpxresearchlib.models.TransactionItem;
import com.robuxearny.official.R;
import com.robuxearny.official.utils.CPX;

import java.util.List;

public class SurveyActivity extends AppCompatActivity implements CPXResearchListener {

    private CPX app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(this, Appodeal.BANNER_VIEW);

        MaterialToolbar tbToolBar = findViewById(R.id.survey_tb_toolbar);
        tbToolBar.setNavigationOnClickListener(v -> finish());

        app = new CPX(FirebaseAuth.getInstance().getUid());

        ViewGroup parentView = findViewById(R.id.ll_container);

        app.getCpxResearch().registerListener(this);
        app.getCpxResearch().setSurveyVisibleIfAvailable(true, this);

        CPXCardConfiguration cardConfig = new CPXCardConfiguration.Builder()
                .accentColor(Color.parseColor("#4800AA"))
                .backgroundColor(Color.parseColor("#FFFFFF"))
                .starColor(Color.parseColor("#FFAA00"))
                .inactiveStarColor(Color.parseColor("#838393"))
                .textColor(Color.parseColor("#8E8E93"))
                .dividerColor(Color.parseColor("#9966CB"))
                .cornerRadius(4f)
                .cpxCardStyle(CPXCardStyle.SMALL)
                .fixedCPXCardWidth(146)
                .currencyPrefixImage(R.drawable.robux) // set your currency image here!!!
                .hideCurrencyName(true)
                .build();

        app.getCpxResearch().insertCPXResearchCardsIntoContainer(this, parentView, cardConfig);
    }

    @Override
    public void onSurveysDidClose() {
        Log.d("CPX", "Surveys did close.");
    }

    @Override
    public void onSurveysDidOpen() {
        Log.d("CPX", "Surveys did open.");
    }

    @Override
    public void onSurveysUpdated() {
        List<SurveyItem> surveys = app.getCpxResearch().getSurveys();
        Log.d("CPX", "Surveys updated: " + surveys);
    }

    @Override
    public void onSurveyDidClose() { Log.d("CPX", "Single survey closed."); }

    @Override
    public void onSurveyDidOpen() { Log.d("CPX", "Single survey opened."); }

    @Override
    public void onTransactionsUpdated(List<TransactionItem> unpaidTransactions) {
        Log.d("CPX", String.format("Transactions updated with %d items", unpaidTransactions.size()));
        for (int i = 0; i < unpaidTransactions.size(); i++) {
            Log.d("CPX", unpaidTransactions.get(i).getEarningPublisher());
        }
    }
}