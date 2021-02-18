package com.example.demo.service;

import com.example.demo.internal.Constants;
import com.example.demo.internal.HelperMethods;
import com.example.demo.internal.Validator;
import com.example.demo.model.ValidationResult;
import com.example.demo.model.cookeryBook.CookeryBook;
import com.example.demo.model.cookeryBook.request.BookCreateModel;
import com.example.demo.model.recipe.Recipe;
import com.example.demo.model.user.User;
import com.example.demo.respository.CookeryBookRepository;
import com.example.demo.respository.RecipeRepository;
import com.example.demo.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
public class CookeryBookService {
    @Autowired
    private CookeryBookRepository cookeryBookRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private Validator validator;
    @Autowired
    private CommonUtils commonUtils;
    @Autowired
    private HelperMethods helperMethods;

    public String getAllBooks(Model model) {
        model.addAttribute(Constants.TITLE,"All public books");
        model.addAttribute(Constants.BOOKS, helperMethods.filterCollection(cookeryBookRepository.findAll(),
                cookeryBook -> isBookViewNotAllowed(cookeryBook, model)));
        return Constants.TMPL_BOOKS;
    }

    public String getBook(Long bookId, Model model) {
        model.addAttribute(Constants.TITLE, "Book");
        CookeryBook book = cookeryBookRepository.findById(bookId).orElse(null);
        if (!isBookViewNotAllowed(book, model)) {
            return Constants.REDIRECT + Constants.BOOK_CREATE;
        }

        model.addAttribute(Constants.BOOK, book);
        model.addAttribute(Constants.RECIPES, recipeRepository.findAllById(book.getRecipeIds()));
        return Constants.TMPL_BOOK;
    }

    public String createBook(BookCreateModel requestModel, Model model) {
        model.addAttribute(Constants.TITLE,"Create book");

        ValidationResult<String> validation = validator.validateBook(requestModel);
        if (!validation.isSuccess()) {
            model.addAttribute(Constants.ERROR, validation.getResult());
            return Constants.TMPL_CREATEBOOK;
        }

        User user = HelperMethods.getLoggedInUser(model);
        CookeryBook cookeryBook = new CookeryBook();
        cookeryBook.setName(requestModel.getName());
        cookeryBook.setUsername(requestModel.getUsername());
        cookeryBook.setUserId(requestModel.getUserId());
        cookeryBook.setPublic(requestModel.isPublic());
        cookeryBook.setUserId(user.getId());
        cookeryBook.setUsername(user.getUsername());

        cookeryBookRepository.save(cookeryBook);
        return Constants.REDIRECT + Constants.BOOKS;
    }

    public String getEditBookById(Long id, Model model) {
        model.addAttribute(Constants.TITLE, "Edit Recipe");
        CookeryBook cookeryBook = cookeryBookRepository.findById(id).orElse(null);
        if (cookeryBook == null || !commonUtils.isOwnerOrAdmin(HelperMethods.getLoggedInUser(model), cookeryBook))
        {
            return Constants.REDIRECT + Constants.BOOKS;
        }

        model.addAttribute(Constants.BOOK, cookeryBook);
        return Constants.TMPL_BOOKEDIT;
    }

    public String editBook(BookCreateModel requestModel, Long id, Model model) {
        model.addAttribute(Constants.TITLE,"Edit book");
        CookeryBook book = cookeryBookRepository.findById(id).orElse(null);
        if (book == null || !commonUtils.isOwnerOrAdmin(HelperMethods.getLoggedInUser(model), book)) {
            return Constants.REDIRECT + Constants.BOOKS;
        }

        ValidationResult<String> validation = validator.validateBook(requestModel);
        if (!validation.isSuccess()) {
            model.addAttribute(Constants.ERROR, validation.getResult());
            return Constants.TMPL_BOOKS;
        }

        book.setName(requestModel.getName());

        if (requestModel.isPublic() != book.isPublic()) {
            book.setPublic(requestModel.isPublic());
            List<Recipe> recipes = helperMethods.getAsCollection(recipeRepository.findAllById(book.getRecipeIds()));

            for (Recipe recipe : recipes) {
                recipe.setPublic(book.isPublic());
            }

            recipeRepository.saveAll(recipes);
        }

        cookeryBookRepository.save(book);
        return Constants.REDIRECT + Constants.BOOKS;
    }

    public String deleteById(Long bookId, Model model) {
        model.addAttribute(Constants.TITLE, "Books");
        CookeryBook cookeryBook = cookeryBookRepository.findById(bookId).orElse(null);
        if (cookeryBook == null || !commonUtils.isOwnerOrAdmin(HelperMethods.getLoggedInUser(model), cookeryBook)) {
            return Constants.REDIRECT + Constants.BOOKS;
        }

        cookeryBookRepository.deleteById(bookId);
        return Constants.REDIRECT + Constants.BOOKS;
    }

    private boolean isBookViewNotAllowed(CookeryBook cookeryBook, Model model) {
        return cookeryBook != null && (cookeryBook.isPublic() || commonUtils.isOwnerOrAdmin(HelperMethods.getLoggedInUser(model), cookeryBook));
    }
}
