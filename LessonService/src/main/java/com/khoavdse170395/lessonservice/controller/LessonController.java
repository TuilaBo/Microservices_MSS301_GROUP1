package com.khoavdse170395.lessonservice.controller;

import com.khoavdse170395.lessonservice.dto.ApiResponse;
import com.khoavdse170395.lessonservice.entity.Lesson;
import com.khoavdse170395.lessonservice.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Lesson Management", description = "API quản lý giáo án môn Ngữ văn")
public class LessonController {

    private final LessonService lessonService;

    // Public endpoint for testing (no JWT required)
    @GetMapping("/public/health")
    @Operation(summary = "Health check", description = "Endpoint công khai để kiểm tra service")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("LessonService is running!", "OK"));
    }

    // Public endpoint to get all lessons without authentication for testing
    @GetMapping("/public/all")
    @Operation(summary = "Lấy tất cả bài học (public)", description = "Endpoint công khai để test")
    public ResponseEntity<ApiResponse<List<Lesson>>> getAllLessonsPublic() {
        List<Lesson> lessons = lessonService.getAllLessons();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách bài học thành công", lessons));
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả bài học", description = "Trả về danh sách tất cả các bài học trong hệ thống")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách bài học thành công")
    public ResponseEntity<ApiResponse<List<Lesson>>> getAllLessons() {
        List<Lesson> lessons = lessonService.getAllLessons();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách bài học thành công", lessons));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy bài học theo ID", description = "Trả về thông tin chi tiết của một bài học")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tìm thấy bài học"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy bài học")
    })
    public ResponseEntity<ApiResponse<Lesson>> getLessonById(
            @Parameter(description = "ID của bài học", required = true)
            @PathVariable String id) {
        Optional<Lesson> lesson = lessonService.getLessonById(id);
        if (lesson.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("Tìm thấy bài học thành công", lesson.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy bài học với ID: " + id));
        }
    }

    @PostMapping
    @Operation(summary = "Tạo bài học mới", description = "Tạo một bài học mới trong hệ thống")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo bài học thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ")
    })
    public ResponseEntity<ApiResponse<Lesson>> createLesson(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin bài học cần tạo")
            @RequestBody Lesson lesson) {
        Lesson createdLesson = lessonService.createLesson(lesson);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo bài học thành công", createdLesson));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật bài học", description = "Cập nhật thông tin của một bài học đã có")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật bài học thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy bài học"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ")
    })
    public ResponseEntity<ApiResponse<Lesson>> updateLesson(
            @Parameter(description = "ID của bài học cần cập nhật", required = true)
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin bài học cần cập nhật")
            @RequestBody Lesson lessonDetails) {
        Lesson updatedLesson = lessonService.updateLesson(id, lessonDetails);
        if (updatedLesson != null) {
            return ResponseEntity.ok(ApiResponse.success("Cập nhật bài học thành công", updatedLesson));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy bài học với ID: " + id));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa bài học", description = "Xóa một bài học khỏi hệ thống")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xóa bài học thành công"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy bài học")
    })
    public ResponseEntity<ApiResponse<String>> deleteLesson(
            @Parameter(description = "ID của bài học cần xóa", required = true)
            @PathVariable String id) {
        boolean deleted = lessonService.deleteLesson(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("Xóa bài học thành công", "Đã xóa bài học với ID: " + id));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy bài học với ID: " + id));
        }
    }

    @GetMapping("/grade/{gradeLevel}")
    @Operation(summary = "Lấy bài học theo lớp", description = "Trả về danh sách bài học của một lớp cụ thể")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách bài học theo lớp thành công")
    public ResponseEntity<ApiResponse<List<Lesson>>> getLessonsByGradeLevel(
            @Parameter(description = "Lớp học (6, 7, 8, 9, 10, 11, 12)", required = true)
            @PathVariable Integer gradeLevel) {
        List<Lesson> lessons = lessonService.getLessonsByGradeLevel(gradeLevel);
        return ResponseEntity.ok(ApiResponse.success("Lấy bài học lớp " + gradeLevel + " thành công", lessons));
    }

    @GetMapping("/type/{lessonType}")
    @Operation(summary = "Lấy bài học theo loại", description = "Trả về danh sách bài học theo loại (Văn học, Tiếng Việt, Tập làm văn)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách bài học theo loại thành công")
    public ResponseEntity<ApiResponse<List<Lesson>>> getLessonsByType(
            @Parameter(description = "Loại bài học (Văn học, Tiếng Việt, Tập làm văn)", required = true)
            @PathVariable String lessonType) {
        List<Lesson> lessons = lessonService.getLessonsByType(lessonType);
        return ResponseEntity.ok(ApiResponse.success("Lấy bài học loại '" + lessonType + "' thành công", lessons));
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm bài học", description = "Tìm kiếm bài học theo từ khóa trong tiêu đề hoặc nội dung")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tìm kiếm bài học thành công")
    public ResponseEntity<ApiResponse<List<Lesson>>> searchLessons(
            @Parameter(description = "Từ khóa tìm kiếm", required = true)
            @RequestParam String keyword) {
        List<Lesson> lessons = lessonService.searchLessons(keyword);
        return ResponseEntity.ok(ApiResponse.success("Tìm kiếm với từ khóa '" + keyword + "' thành công", lessons));
    }

    @GetMapping("/filter")
    @Operation(summary = "Lọc bài học", description = "Lọc bài học theo lớp và loại bài học")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lọc bài học thành công")
    public ResponseEntity<ApiResponse<List<Lesson>>> getLessonsByGradeLevelAndType(
            @Parameter(description = "Lớp học", required = true)
            @RequestParam Integer gradeLevel,
            @Parameter(description = "Loại bài học", required = true)
            @RequestParam String lessonType) {
        List<Lesson> lessons = lessonService.getLessonsByGradeLevelAndType(gradeLevel, lessonType);
        return ResponseEntity.ok(ApiResponse.success("Lọc bài học lớp " + gradeLevel + " loại '" + lessonType + "' thành công", lessons));
    }

    @GetMapping("/duration")
    @Operation(summary = "Lọc bài học theo thời lượng", description = "Lọc bài học theo khoảng thời gian (phút)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lọc bài học theo thời lượng thành công")
    public ResponseEntity<ApiResponse<List<Lesson>>> getLessonsByDurationRange(
            @Parameter(description = "Thời lượng tối thiểu (phút)", required = true)
            @RequestParam Integer minDuration,
            @Parameter(description = "Thời lượng tối đa (phút)", required = true)
            @RequestParam Integer maxDuration) {
        List<Lesson> lessons = lessonService.getLessonsByDurationRange(minDuration, maxDuration);
        return ResponseEntity.ok(ApiResponse.success("Lọc bài học từ " + minDuration + " đến " + maxDuration + " phút thành công", lessons));
    }
}
