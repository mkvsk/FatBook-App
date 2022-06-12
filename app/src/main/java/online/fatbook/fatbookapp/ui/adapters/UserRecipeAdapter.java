package online.fatbook.fatbookapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import online.fatbook.fatbookapp.core.Recipe;

import java.util.List;

public class UserRecipeAdapter extends RecyclerView.Adapter<UserRecipeAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Recipe> list;

    public UserRecipeAdapter(Context context, List<Recipe> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public UserRecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull UserRecipeAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
