package com.fatbook.fatbookapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Ingredient;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.core.User;
import com.fatbook.fatbookapp.ui.listeners.OnRecipeClickListener;
import com.fatbook.fatbookapp.util.RecipeUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.logging.Level;

import lombok.extern.java.Log;

@Log
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private List<Recipe> list;
    private User user;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(Context context, List<Recipe> list, User user, OnRecipeClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.user = user;
        this.listener = listener;
    }

    public void setData(List<Recipe> list, User user) {
        this.list = list;
        this.user = user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void updateList(List<Recipe> list) {
        this.list = list;
        notifyDataSetChanged();
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
        holder.tvAuthor.setText(recipe.getAuthor());

        String forksAmount = Integer.toString(recipe.getForks());
        holder.tvForks.setText(forksAmount);

        toggleForks(holder.fork, user.getRecipesForked().contains(recipe.getIdentifier()));
        if (recipe.getAuthor().equals(user.getLogin())) {
            holder.bookmarks.setVisibility(View.INVISIBLE);
        } else {
            toggleBookmarks(holder.bookmarks, user.getRecipesBookmarked().contains(recipe.getIdentifier()));
        }

        if (StringUtils.isNotEmpty(recipe.getImage())) {
            Glide
                    .with(inflater.getContext())
                    .load(recipe.getImage())
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.image_recipe_default);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void toggleForks(ImageView fork, boolean selected) {
        if (selected) {
            fork.setImageResource(R.drawable.icon_fork_checked);
            fork.setTag(RecipeUtils.TAG_FORK_CHECKED);
        } else {
            fork.setImageResource(R.drawable.icon_fork_unchecked);
            fork.setTag(RecipeUtils.TAG_FORK_UNCHECKED);
        }
    }

    private void toggleBookmarks(ImageView bookmark, boolean selected) {
        if (selected) {
            bookmark.setImageResource(R.drawable.icon_bookmarks_checked);
            bookmark.setTag(RecipeUtils.TAG_BOOKMARKS_CHECKED);
        } else {
            bookmark.setImageResource(R.drawable.icon_bookmarks_unchecked);
            bookmark.setTag(RecipeUtils.TAG_BOOKMARKS_UNCHECKED);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final TextView tvAuthor;
        final TextView tvForks;
        final ImageView image;
        final ImageView bookmarks;
        final ImageView fork;
        final ImageButton btnRecipe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.textView_rv_card_recipe_title);
            tvAuthor = itemView.findViewById(R.id.textView_rv_card_recipe_author);
            tvForks = itemView.findViewById(R.id.textView_rv_card_recipe_forks_avg);
            image = itemView.findViewById(R.id.imageView_rv_card_recipe_photo);
            bookmarks = itemView.findViewById(R.id.imageView_rv_card_recipe_bookmarks);
            fork = itemView.findViewById(R.id.imageView_rv_card_recipe_fork);
            btnRecipe = itemView.findViewById(R.id.rv_card_recipe_bgr);

            btnRecipe.setOnClickListener(view -> listener.onRecipeClick(getAdapterPosition()));

            bookmarks.setOnClickListener(_view -> {
                String tag = (String) bookmarks.getTag();
                switch (tag) {
                    case RecipeUtils.TAG_BOOKMARKS_UNCHECKED:
                        toggleBookmarks(bookmarks, true);
                        listener.onBookmarksClick(list.get(getAdapterPosition()), true, getAdapterPosition());
                        break;
                    case RecipeUtils.TAG_BOOKMARKS_CHECKED:
                        toggleBookmarks(bookmarks, false);
                        listener.onBookmarksClick(list.get(getAdapterPosition()), false, getAdapterPosition());
                        break;
                }

            });

            fork.setOnClickListener(_view -> {
                String tag = (String) fork.getTag();
                switch (tag) {
                    case RecipeUtils.TAG_FORK_UNCHECKED:
                        toggleForks(fork, true);
                        listener.onForkClicked(list.get(getAdapterPosition()), true, getAdapterPosition());
                        break;
                    case RecipeUtils.TAG_FORK_CHECKED:
                        toggleForks(fork, false);
                        listener.onForkClicked(list.get(getAdapterPosition()), false, getAdapterPosition());
                        break;

                }
            });
        }
    }
}
