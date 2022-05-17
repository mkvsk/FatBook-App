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
import com.fatbook.fatbookapp.core.IngredientUnit;

import java.util.List;

public class RecipeAddIngredientAdapter  extends RecyclerView.Adapter<RecipeAddIngredientAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private List<Ingredient> list;

    private Context context;
    private int lastClickedPosition;
    private int selectedItem;

    public RecipeAddIngredientAdapter(Context context, List<Ingredient> list) {
        this.context = context;
        selectedItem = -1;

        this.list = list;
        this.inflater = LayoutInflater.from(context);
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

        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
        if (selectedItem == position) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.color_lime_500));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int previousItem = selectedItem;
                selectedItem = position;

               // lastClickedPosition = selectedItem;

                notifyItemChanged(previousItem);
                notifyItemChanged(position);
            }
        });

       // holder.tvSelectedIngredient.setText();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvIngredient;
        final CardView cardView;
        final TextView tvSelectedIngredient;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredient = itemView.findViewById(R.id.textView_ingredient_to_recipe);
            cardView = itemView.findViewById(R.id.rv_item_card);
            tvSelectedIngredient = itemView.findViewById(R.id.textView_selected_ingredient);
        }
    }
}
