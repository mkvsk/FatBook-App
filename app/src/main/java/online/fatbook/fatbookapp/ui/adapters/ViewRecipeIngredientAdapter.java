package online.fatbook.fatbookapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fatbook.fatbookapp.R;
import online.fatbook.fatbookapp.core.RecipeIngredient;
import online.fatbook.fatbookapp.ui.listeners.OnRecipeViewDeleteIngredient;

import java.util.List;

public class ViewRecipeIngredientAdapter extends RecyclerView.Adapter<ViewRecipeIngredientAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private List<RecipeIngredient> list;

    private boolean isEditAvailable = false;

    private OnRecipeViewDeleteIngredient listener;

    public ViewRecipeIngredientAdapter(Context context, List<RecipeIngredient> list, OnRecipeViewDeleteIngredient listener) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_add_new_recipe_ingredients, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeIngredient ingredient = list.get(position);
        holder.name.setText(ingredient.getIngredient().getName());
        String quantityName = ingredient.getQuantity() + " " + ingredient.getUnit().getDisplayName(inflater.getContext());
        holder.quantity.setText(quantityName);
        if (isEditAvailable) {
            holder.btnRemove.setVisibility(View.VISIBLE);
        } else {
            holder.btnRemove.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setEditMode(boolean allow) {
        isEditAvailable = allow;
    }

    public void setData(List<RecipeIngredient> list) {
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView quantity;
        final ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView_ingredient_in_recipe);
            quantity = itemView.findViewById(R.id.textView_rv_add_recipe_ingredient_quantity);
            btnRemove = itemView.findViewById(R.id.btn_rv_add_recipe_ingredient_remove);
            btnRemove.setOnClickListener(view -> {
                listener.onDeleteIngredientClick(list.get(getAdapterPosition()), getAdapterPosition());
            });
        }
    }
}
