package com.robuxearny.official.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.robuxearny.official.R;
import com.robuxearny.official.data.IntroSlide;

import java.util.List;

public class IntroSliderAdapter extends PagerAdapter {

    private final Context context;
    private final List<IntroSlide> introSlides;
    private final ActivityResultLauncher<IntentSenderRequest> oneTapLauncher;
    private final SignInClient oneTapClient;

    public IntroSliderAdapter(Context context, List<IntroSlide> introSlides, ActivityResultLauncher<IntentSenderRequest> oneTapLauncher, SignInClient oneTapClient) {
        this.context = context;
        this.introSlides = introSlides;
        this.oneTapLauncher = oneTapLauncher;
        this.oneTapClient = oneTapClient;
    }

    private BeginSignInRequest signUpRequest;

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {


        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.intro_slide, container, false);


        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);
        AdView adView = view.findViewById(R.id.adView);
        AdView adView2 = view.findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView2.loadAd(adRequest);

        IntroSlide introSlide = introSlides.get(position);

        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(context.getString(R.string.web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();


        if (position == introSlides.size() - 1) {
            Button startButton = view.findViewById(R.id.startButton);
            startButton.setVisibility(View.VISIBLE);

            startButton.setOnClickListener(v ->
                    oneTapClient.beginSignIn(signUpRequest)
                            .addOnSuccessListener((Activity) context, beginSignInResult -> {
                                IntentSenderRequest intentSenderRequest =
                                        new IntentSenderRequest.Builder(beginSignInResult.getPendingIntent().getIntentSender()).build();
                                oneTapLauncher.launch(intentSenderRequest);
                            })
                            .addOnFailureListener((Activity) context, e -> Log.d("Login", e.getMessage())));
        }

        titleTextView.setText(introSlide.getTitle());
        descriptionTextView.setText(introSlide.getDescription());

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return introSlides.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
