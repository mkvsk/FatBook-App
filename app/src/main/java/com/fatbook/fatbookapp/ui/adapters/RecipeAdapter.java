package com.fatbook.fatbookapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final List<Recipe> list;

    public RecipeAdapter(Context context, List<Recipe> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_feed_recipe_card_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = list.get(position);
        holder.tvTitle.setText(recipe.getName());
        holder.tvAuthor.setText(recipe.getAuthor().getLogin());
        holder.tvForks.setText(recipe.getForks().toString());

        Glide
                .with(inflater.getContext())
                .load(recipe.getImage())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final TextView tvAuthor;
        final TextView tvForks;
        final ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.textView_rv_card_recipe_title);
            tvAuthor = itemView.findViewById(R.id.textView_rv_card_recipe_author);
            tvForks = itemView.findViewById(R.id.textView_rv_card_recipe_forks_avg);
            image = itemView.findViewById(R.id.imageView_rv_card_recipe_photo);
        }
    }
}
