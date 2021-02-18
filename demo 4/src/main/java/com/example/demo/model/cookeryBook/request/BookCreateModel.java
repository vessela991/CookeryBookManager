package com.example.demo.model.cookeryBook.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.ElementCollection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookCreateModel {
    private String name;
    private Long userId;
    private String username;
    private boolean isPublic;
}
