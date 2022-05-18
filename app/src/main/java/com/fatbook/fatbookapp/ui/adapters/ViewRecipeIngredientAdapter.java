package com.fatbook.fatbookapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.R;
import com.fatbook.fatbookapp.core.RecipeIngredient;

import java.util.List;

public class ViewRecipeIngredientAdapter extends RecyclerView.Adapter<ViewRecipeIngredientAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<RecipeIngredient> list;

    public ViewRecipeIngredientAdapter(Context context, List<RecipeIngredient> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_recipe_view_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeIngredient ingredient = list.get(position);
        holder.name.setText(ingredient.getIngredient().getName());
//        String quantityName = ingredient.getQuantity() + ingredient.getUnit().getDisplayName();
//        holder.quantity.setText(quantityName);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
//        final TextView quantity;
//        final Button btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView_recipe_view_ingredient);
//            quantity = itemView.findViewById(R.id.editText_rv_add_recipe_ingredient_quantity);
//            btnRemove = itemView.findViewById(R.id.btn_rv_add_recipe_ingredient_remove);
//            btnRemove.setOnClickListener(view -> {
//                System.out.println();
//            });
        }
    }
}
