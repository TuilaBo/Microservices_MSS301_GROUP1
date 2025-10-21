package com.khoavdse170395.lessonservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Định dạng phản hồi chuẩn cho API")
public class ApiResponse<T> {

    @Schema(description = "Thông báo kết quả", example = "Lấy dữ liệu thành công")
    private String message;



    @Schema(description = "Dữ liệu trả về")
    private T data;

    // Static methods để tạo response dễ dàng
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("Thành công", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, null);
    }
}
