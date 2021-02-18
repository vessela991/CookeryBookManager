package com.example.demo.respository;

import com.example.demo.model.recipe.Recipe;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Long> {
    Optional<Recipe> findByIdAndCookeryBookId(Long recipeId, Long cookeryBookId);
}
