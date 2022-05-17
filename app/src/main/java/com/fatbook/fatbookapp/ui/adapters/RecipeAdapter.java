package com.fatbook.fatbookapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Recipe;
import com.fatbook.fatbookapp.ui.OnRecipeClickListener;
import com.fatbook.fatbookapp.util.RecipeUtils;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Recipe> list;

    private OnRecipeClickListener listener;

    public RecipeAdapter(Context context, List<Recipe> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    public void setClickListener(OnRecipeClickListener listener) {
        this.listener = listener;
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

        String forksAmount = Integer.toString(recipe.getForks());
        holder.tvForks.setText(forksAmount);

        Glide
                .with(inflater.getContext())
                .load(recipe.getImage())
                .into(holder.image);

        holder.bookmarks.setOnClickListener(_view -> {
            String tag = (String) holder.bookmarks.getTag();
            switch (tag) {
                case RecipeUtils.TAG_ADDED_TO_BOOKMARKS:
                    holder.bookmarks.setImageResource(R.drawable.icon_add_fav_grey);
                    holder.bookmarks.setTag(RecipeUtils.TAG_NOT_ADDED_TO_BOOKMARKS);
                    listener.onBookmarksClick(position, false);

                    Toast.makeText(_view.getContext(), "removed from favourites", Toast.LENGTH_SHORT).show();
                    break;
                case RecipeUtils.TAG_NOT_ADDED_TO_BOOKMARKS:
                    holder.bookmarks.setImageResource(R.drawable.icon_add_fav_pink);
                    holder.bookmarks.setTag(RecipeUtils.TAG_ADDED_TO_BOOKMARKS);
                    listener.onBookmarksClick(position, true);

                    Toast.makeText(_view.getContext(), "added to favourites", Toast.LENGTH_SHORT).show();
                    break;
            }

        });

        holder.fork.setOnClickListener(_view -> {
            String tag = (String) holder.fork.getTag();
            switch (tag) {
                case RecipeUtils.TAG_FORKED:
                    holder.fork.setImageResource(R.drawable.icon_fork_grey);
                    holder.fork.setTag(RecipeUtils.TAG_NOT_FORKED);
                    listener.onForkClicked(position, false);

                    Toast.makeText(_view.getContext(), "no forked FAK U", Toast.LENGTH_SHORT).show();
                    break;
                case RecipeUtils.TAG_NOT_FORKED:
                    holder.fork.setImageResource(R.drawable.icon_fork_pink);
                    holder.fork.setTag(RecipeUtils.TAG_FORKED);
                    listener.onForkClicked(position, true);

                    Toast.makeText(_view.getContext(), "forked", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
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
        }
    }
}
