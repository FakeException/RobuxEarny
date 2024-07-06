/*
 * Created by Fake on 7/6/24, 5:30 PM
 * Copyright (c) 2024. All rights reserved.
 * Last modified 7/6/24, 5:30 PM
 */

package com.robuxearny.official.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.robuxearny.official.R;
import com.robuxearny.official.models.FAQItem;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {

    private final List<FAQItem> faqItems;

    public FAQAdapter(List<FAQItem> faqItems) {
        this.faqItems = faqItems;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faq_item, parent, false);
        return new FAQViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder holder, int position) {
        FAQItem faqItem = faqItems.get(position);
        holder.questionTextView.setText(faqItem.question);
        holder.answerTextView.setText(faqItem.answer);
    }

    @Override
    public int getItemCount() {
        return faqItems.size();
    }

    public static class FAQViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;
        TextView answerTextView;

        FAQViewHolder(View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            answerTextView = itemView.findViewById(R.id.answerTextView);
        }
    }
}