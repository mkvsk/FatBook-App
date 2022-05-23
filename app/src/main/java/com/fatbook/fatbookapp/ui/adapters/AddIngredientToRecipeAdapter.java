package com.fatbook.fatbookapp.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.Ingredient;
import com.fatbook.fatbookapp.ui.listeners.OnAddIngredientItemClickListener;

import java.util.List;

public class AddIngredientToRecipeAdapter extends RecyclerView.Adapter<AddIngredientToRecipeAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private List<Ingredient> list;

    private int selectedItem = -1;

    private OnAddIngredientItemClickListener listener;

    public AddIngredientToRecipeAdapter(Context context, List<Ingredient> list) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    public void setClickListener(OnAddIngredientItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_ingredient_to_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Ingredient ingredient = list.get(position);
        holder.tvIngredient.setText(ingredient.getName());

        holder.cardView.setCardBackgroundColor(inflater.getContext().getResources().getColor(R.color.white));
        if (selectedItem == position) {
            holder.cardView.setCardBackgroundColor(inflater.getContext().getResources().getColor(R.color.color_lime_500));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<Ingredient> list) {
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvIngredient;
        final CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredient = itemView.findViewById(R.id.textView_ingredient_to_recipe);
            cardView = itemView.findViewById(R.id.rv_item_card);
            itemView.setOnClickListener(view -> {
                int previousItem = selectedItem;
                selectedItem = getAdapterPosition();
                listener.onIngredientClick(previousItem, selectedItem, list.get(getAdapterPosition()));
            });
        }
    }
}
