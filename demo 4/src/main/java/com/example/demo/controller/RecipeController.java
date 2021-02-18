package com.example.demo.controller;

import com.example.demo.internal.Constants;
import com.example.demo.model.recipe.Recipe;
import com.example.demo.model.recipe.request.RecipeCreateModel;
import com.example.demo.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RecipeController {
    @Autowired
    private RecipeService recipeService;

    @GetMapping("/recipes")
    public String getAllRecipes(@ModelAttribute("recipe") Recipe recipes, Model model) {
        return recipeService.getAllRecipes(model);
    }

    @GetMapping("/recipes/{id}")
    public String getRecipe(@ModelAttribute("recipe") Recipe recipes, @PathVariable(value = "id") Long id, Model model) {
        return recipeService.getRecipe(id, model);
    }

    @GetMapping("/recipes/create")
    public String getCreateRecipe(@ModelAttribute("recipe") RecipeCreateModel recipe, Model model) {
        return recipeService.getCreateRecipe(model);
    }

    @PostMapping("/recipes/create")
    public String createRecipe(@ModelAttribute("recipe") RecipeCreateModel recipe, Model model) {
        return recipeService.create(recipe, model);
    }

    @GetMapping("/recipes/{id}/edit")
    public String getEditRecipe(@ModelAttribute("recipe") RecipeCreateModel recipe, @PathVariable(value = "id") Long recipeId, Model model) {
        return recipeService.getEdit(recipeId, model);
    }

    @PostMapping("/recipes/{id}/edit")
    public String editRecipe(@ModelAttribute("recipe") RecipeCreateModel recipe, @PathVariable("id") Long id, Model model) {
        return recipeService.edit(recipe, id, model);
    }

    @PostMapping("/recipes/{id}/delete")
    public String deleteRecipe(@PathVariable("id") Long id, Model model) {
        return recipeService.delete(id, model);
    }
}