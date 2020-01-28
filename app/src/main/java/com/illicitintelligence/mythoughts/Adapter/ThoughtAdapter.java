package com.illicitintelligence.mythoughts.Adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.illicitintelligence.mythoughts.R;
import com.illicitintelligence.mythoughts.model.Thought;

import java.util.List;

public class ThoughtAdapter extends RecyclerView.Adapter<ThoughtAdapter.ThoughtViewHolder> {

    private List<Thought> thoughts;

    public ThoughtAdapter(List<Thought> thoughts) {
        this.thoughts = thoughts;
    }

    @NonNull
    @Override
    public ThoughtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thought_item_layout, parent, false);
        return new ThoughtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThoughtViewHolder holder, int position) {

        holder.sharedByTextView.setText(thoughts.get(position).getSharedBy());
        holder.thoughtTextView.setText(thoughts.get(position).getSharedThought());

        Glide.with(holder.itemView.getContext())
                .applyDefaultRequestOptions(RequestOptions.centerCropTransform())
                .load(Uri.parse(thoughts.get(position).getSharedImage()))
                .into(holder.thoughtImage);
    }

    @Override
    public int getItemCount() {
        return thoughts.size();
    }

    class ThoughtViewHolder extends RecyclerView.ViewHolder {
        private TextView thoughtTextView;
        private TextView sharedByTextView;
        private ImageView thoughtImage;

        public ThoughtViewHolder(@NonNull View itemView) {
            super(itemView);

            thoughtImage = itemView.findViewById(R.id.thought_image_view);
            thoughtTextView = itemView.findViewById(R.id.thought_textview);
            sharedByTextView = itemView.findViewById(R.id.shared_by_textview);

        }
    }


}
