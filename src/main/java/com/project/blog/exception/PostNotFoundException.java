package com.project.blog.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super("Post Not Found Exception: " + message);
    }
}
