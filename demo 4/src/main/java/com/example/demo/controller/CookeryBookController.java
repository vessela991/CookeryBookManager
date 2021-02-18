package com.example.demo.controller;

import com.example.demo.internal.Constants;
import com.example.demo.model.cookeryBook.CookeryBook;
import com.example.demo.model.cookeryBook.request.BookCreateModel;
import com.example.demo.service.CookeryBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CookeryBookController {
    @Autowired
    private CookeryBookService cookeryBookService;

    @GetMapping("/books")
    public String getAllBooks(@ModelAttribute("book") CookeryBook book, Model model) {
        return cookeryBookService.getAllBooks(model);
    }

    @GetMapping("/books/{id}")
    public String getBookById(@ModelAttribute("book") CookeryBook book, @PathVariable("id") Long id, Model model) {
        return cookeryBookService.getBook(id, model);
    }

    @GetMapping("/books/createbook")
    public String createBookGet(@ModelAttribute("book") BookCreateModel bookCreateModel, Model model) {
        model.addAttribute(Constants.TITLE, "Create book");
        return Constants.TMPL_CREATEBOOK;
    }

    @PostMapping("/books/createbook")
    public String createBook(@ModelAttribute("book") BookCreateModel bookCreateModel, Model model) {
        return cookeryBookService.createBook(bookCreateModel, model);
    }

    @GetMapping("/books/{id}/edit")
    public String getEditBookById(@ModelAttribute("book") BookCreateModel bookCreateModel, @PathVariable("id") Long id, Model model) {
        return cookeryBookService.getEditBookById(id, model);
    }

    @PostMapping("/books/{id}/edit")
    public String postEditBookById(@ModelAttribute("book") BookCreateModel bookCreateModel, @PathVariable("id") Long id, Model model) {
        return cookeryBookService.editBook(bookCreateModel, id, model);
    }

    @PostMapping(value = "/books/{id}/delete")
    public String deleteBook(@PathVariable("id") Long bookId, Model model) {
        return cookeryBookService.deleteById(bookId, model);
    }
}
