package com.example.demo.model;

import lombok.Getter;

@Getter
public class ValidationResult<T>  {
    private final boolean success;
    private T result;

    public ValidationResult(boolean success) {
        this.success = success;
    }

    public ValidationResult(boolean success, T result) {
        this.success = success;
        this.result = result;
    }
}
