package com.fatbook.fatbookapp.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Recipe;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Recipe> list;
    private final OnItemClickListener listener;

    public RecipeAdapter(Context context, List<Recipe> list, OnItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_feed_recipe_card_preview, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

//        viewHolder.btnRecipe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(view.getContext(), "test", Toast.LENGTH_SHORT).show();
//            }
//        });

        viewHolder.bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = (String) viewHolder.bookmarks.getTag();
                switch (tag) {
                    case "fav":
                        Toast.makeText(view.getContext(), "removed from favourites", Toast.LENGTH_SHORT).show();
                        viewHolder.bookmarks.setImageResource(R.drawable.icon_add_fav_grey);
                        viewHolder.bookmarks.setTag("nofav");
                        break;
                    case "nofav":
                        Toast.makeText(view.getContext(), "added to favourites", Toast.LENGTH_SHORT).show();
                        viewHolder.bookmarks.setImageResource(R.drawable.icon_add_fav_pink);
                        viewHolder.bookmarks.setTag("fav");
                        break;
                }

            }
        });

        viewHolder.fork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = (String) viewHolder.fork.getTag();
                switch (tag) {
                    case "fork":
                        Toast.makeText(view.getContext(), "no forked FAK U", Toast.LENGTH_SHORT).show();
                        viewHolder.fork.setImageResource(R.drawable.icon_fork_grey);
                        viewHolder.fork.setTag("nofork");
                        break;
                    case "nofork":
                        Toast.makeText(view.getContext(), "forked", Toast.LENGTH_SHORT).show();
                        viewHolder.fork.setImageResource(R.drawable.icon_fork_pink);
                        viewHolder.fork.setTag("fork");
                        break;
                }
            }
        });
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
        final ImageView bookmarks;
        final ImageView fork;
        final ImageButton btnRecipe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.textView_rv_card_recipe_title);
            tvAuthor = itemView.findViewById(R.id.textView_rv_card_recipe_author);
            tvForks = itemView.findViewById(R.id.textView_rv_card_recipe_forks_avg);
            image = itemView.findViewById(R.id.imageView_rv_card_recipe_photo);
            bookmarks = itemView.findViewById(R.id.imageView_rv_card_recipe_fav);
            fork = itemView.findViewById(R.id.imageView_rv_card_recipe_fork);
            btnRecipe = itemView.findViewById(R.id.rv_card_recipe_bgr);
        }
    }
}
