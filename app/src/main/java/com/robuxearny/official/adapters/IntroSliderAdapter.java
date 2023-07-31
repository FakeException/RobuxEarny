package com.robuxearny.official.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.MainMenuActivity;
import com.robuxearny.official.data.IntroSlide;
import com.robuxearny.official.network.api.Backend;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class IntroSliderAdapter extends PagerAdapter {

    private final Context context;
    private final List<IntroSlide> introSlides;

    public IntroSliderAdapter(Context context, List<IntroSlide> introSlides) {
        this.context = context;
        this.introSlides = introSlides;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.intro_slide, container, false);
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);
        AdView adView = view.findViewById(R.id.adView);
        AdView adView2 = view.findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView2.loadAd(adRequest);


        IntroSlide introSlide = introSlides.get(position);

        if (position == introSlides.size() - 1) {
            Button startButton = view.findViewById(R.id.startButton);
            EditText username = view.findViewById(R.id.username);
            EditText password = view.findViewById(R.id.password);
            startButton.setVisibility(View.VISIBLE);
            username.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);

            startButton.setOnClickListener(v -> {
                String response;
                try {
                    response = Backend.access(username.getText().toString(), password.getText().toString());
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Toast toast;
                if (response.contains("token")) {
                    toast = Toast.makeText(context, "Success!", Toast.LENGTH_LONG);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", response);
                    editor.apply();
                    context.startActivity(new Intent(context, MainMenuActivity.class));
                    ((Activity) this.context).finish();
                } else {
                    toast = Toast.makeText(context, response, Toast.LENGTH_LONG);
                }
                toast.show();

                // Avvia l'attività principale o il menu principale
                //context.startActivity(new Intent(context, MainMenuActivity.class));
               // ((Activity) context).finish();
            });
        }

        titleTextView.setText(introSlide.getTitle());
        descriptionTextView.setText(introSlide.getDescription());

        container.addView(view);

        return view;
    }

    private boolean validateUsername(EditText usernameEdit) {
        String username = usernameEdit.getText().toString().trim();
        if (username.isEmpty()) {
            usernameEdit.setError("Username is required");
            return false;
        } else {
            usernameEdit.setError(null);
            return true;
        }
    }

    private boolean validatePassword(EditText passwordEdit) {
        String password = passwordEdit.getText().toString().trim();
        if (password.isEmpty()) {
            passwordEdit.setError("Password is required");
            return false;
        } else if (password.length() < 8) {
            passwordEdit.setError("Passwordd should be at least 8 characters long");
            return false;
        } else {
            passwordEdit.setError(null);
            return true;
        }
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
