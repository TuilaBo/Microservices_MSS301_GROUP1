package com.khoavdse170395.documentservice.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(400, message, null);
    }
}
