package com.example.demo.service;

import com.example.demo.internal.Constants;
import com.example.demo.internal.HelperMethods;
import com.example.demo.internal.Validator;
import com.example.demo.model.ValidationResult;
import com.example.demo.model.cookeryBook.CookeryBook;
import com.example.demo.model.recipe.Recipe;
import com.example.demo.model.recipe.request.RecipeCreateModel;
import com.example.demo.respository.CookeryBookRepository;
import com.example.demo.respository.RecipeRepository;
import com.example.demo.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class RecipeService {
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private CookeryBookRepository cookeryBookRepository;
    @Autowired
    private Validator validator;
    @Autowired
    private HelperMethods helperMethods;
    @Autowired
    private CommonUtils commonUtils;

    public String getAllRecipes(Model model) {
        model.addAttribute(Constants.TITLE, "Recipes");
        model.addAttribute(Constants.RECIPES, helperMethods.filterCollection(recipeRepository.findAll(),
                recipe -> isRecipeViewNotAllowed(recipe, model)));
        return Constants.TMPL_RECIPES;
    }

    public String getRecipe(Long recipeId, Model model) {
        model.addAttribute(Constants.TITLE, "Recipe");
        Recipe rec = recipeRepository.findById(recipeId).orElse(null);
        model.addAttribute(Constants.RECIPE, rec);
        return Constants.TMPL_RECIPE;
    }

    public String getCreateRecipe(Model model) {
        model.addAttribute(Constants.TITLE, "Create Recipe");
        model.addAttribute(Constants.BOOKS, helperMethods.filterCollection(cookeryBookRepository.findAll(),
                book -> isBookViewAllowed(book, model)));
        return Constants.TMPL_RECIPE_CREATE;
    }

    public String create(RecipeCreateModel requestModel, Model model) {
        model.addAttribute(Constants.TITLE, "Create Recipe");
        ValidationResult<String> validation = validator.validateRecipeCreation(requestModel);
        if (!validation.isSuccess()) {
            model.addAttribute(Constants.ERROR, validation.getResult());
            return Constants.TMPL_RECIPE_CREATE;
        }

        CookeryBook cookeryBook = cookeryBookRepository.findByName(requestModel.getCookeryBookName());
        if (cookeryBook == null) {
            model.addAttribute(Constants.ERROR, "There is no book with such name: " + requestModel.getCookeryBookName());
            return Constants.TMPL_RECIPE_CREATE;
        }

        String picture = HelperMethods.getMultiPartFileAs64BaseString(requestModel.getPicture());
        if (picture == null) {
            model.addAttribute(Constants.ERROR, "System error, please insert another picture");
            return Constants.TMPL_RECIPE_CREATE;
        }

        recipeRepository.save(Recipe.fromRecipeCreateModel(requestModel, picture, cookeryBook));
        return Constants.REDIRECT + Constants.RECIPES;
    }

    public String getEdit(Long recipeId, Model model) {
        model.addAttribute(Constants.TITLE, "Edit Recipe");
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe == null || !commonUtils.isOwnerOrAdmin(HelperMethods.getLoggedInUser(model), recipe))
        {
            return Constants.REDIRECT + Constants.RECIPES;
        }

        model.addAttribute(Constants.RECIPE, recipe);
        return Constants.TMPL_RECIPE_EDIT;
    }

    public String edit(RecipeCreateModel requestModel, Long recipeId, Model model) {
        model.addAttribute(Constants.TITLE, "Edit Recipe");
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe == null) {
            return Constants.REDIRECT + Constants.RECIPES;
        }

        ValidationResult<String> validation = validator.validateRecipe(requestModel);
        if (!validation.isSuccess()) {
            model.addAttribute(Constants.ERROR, validation.getResult());
            return Constants.TMPL_RECIPE_CREATE;
        }

        if (HelperMethods.isPictureUploaded(requestModel.getPicture()))
        {
            String picture = HelperMethods.getMultiPartFileAs64BaseString(requestModel.getPicture());
            if (picture == null) {
                model.addAttribute(Constants.ERROR, "System error, please insert another picture");
                return Constants.TMPL_RECIPE_CREATE;
            }

            recipe.setPicture(picture);
        }

        recipe.setName(requestModel.getName());
        recipe.setLongDescription(requestModel.getLongDescription());
        recipe.setShortDescription(requestModel.getShortDescription());
        List<String> tags = new ArrayList<>();
        Collections.addAll(tags, requestModel.getTags().split(","));
        recipe.setTags(tags);
        recipe.setTimeToCook(requestModel.getTimeToCook());
        List<String> products = new ArrayList<>();
        Collections.addAll(products, requestModel.getProducts().split(","));
        recipe.setProducts(products);

        recipeRepository.save(recipe);
        return Constants.REDIRECT + Constants.RECIPES;
    }

    public String delete(Long id, Model model) {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (isRecipeViewNotAllowed(recipe, model)) {
            recipeRepository.deleteById(id);
        }
        return Constants.REDIRECT + Constants.RECIPES;
    }

    private boolean isRecipeViewNotAllowed(Recipe recipe, Model model) {
        return recipe != null && (recipe.isPublic() || commonUtils.isOwnerOrAdmin(HelperMethods.getLoggedInUser(model), recipe));
    }

    private boolean isBookViewAllowed(CookeryBook cookeryBook, Model model) {
        return cookeryBook != null && commonUtils.isOwnerOrAdmin(HelperMethods.getLoggedInUser(model), cookeryBook);
    }
}
