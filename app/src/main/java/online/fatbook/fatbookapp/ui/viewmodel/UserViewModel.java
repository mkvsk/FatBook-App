package online.fatbook.fatbookapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import online.fatbook.fatbookapp.core.Recipe;
import online.fatbook.fatbookapp.core.User;

import java.util.List;

public class UserViewModel extends ViewModel {

    private final MutableLiveData<User> user = new MutableLiveData<User>();

    private final MutableLiveData<List<Recipe>> recipeList = new MutableLiveData<>();

    private final MutableLiveData<List<Recipe>> bookmarkedRecipeList = new MutableLiveData<>();

    private final MutableLiveData<List<Recipe>> forkedRecipeList = new MutableLiveData<>();

    private final MutableLiveData<List<Recipe>> feedRecipeList = new MutableLiveData<>();

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList.setValue(recipeList);
    }

    public LiveData<List<Recipe>> getRecipeList() {
        return recipeList;
    }

    public void setBookmarkedRecipeList(List<Recipe> bookmarkedRecipeList) {
        this.bookmarkedRecipeList.setValue(bookmarkedRecipeList);
    }

    public LiveData<List<Recipe>> getBookmarkedRecipeList() {
        return bookmarkedRecipeList;
    }

    public void setForkedRecipeList(List<Recipe> forkedRecipeList) {
        this.forkedRecipeList.setValue(forkedRecipeList);
    }

    public LiveData<List<Recipe>> getForkedRecipeList() {
        return forkedRecipeList;
    }

    public void setFeedRecipeList(List<Recipe> feedRecipeList) {
        this.feedRecipeList.setValue(feedRecipeList);
    }

    public LiveData<List<Recipe>> getFeedRecipeList() {
        return feedRecipeList;
    }
}
