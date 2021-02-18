package com.example.demo.model.cookeryBook;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cookeryBooks")
public class CookeryBook {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String name;

    @ElementCollection(targetClass=Long.class)
    private List<Long> recipeIds;

    private Long userId;
    private String username;
    private boolean isPublic;
}
