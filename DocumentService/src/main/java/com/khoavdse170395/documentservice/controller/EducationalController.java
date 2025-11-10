package com.khoavdse170395.documentservice.controller;

import com.khoavdse170395.documentservice.dto.ApiResponse;
import com.khoavdse170395.documentservice.dto.DocumentResponseDto;
import com.khoavdse170395.documentservice.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/educational")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Educational Content", description = "API quản lý nội dung giáo dục")
public class EducationalController {

    private final DocumentService documentService;

    @GetMapping("/lesson-materials/{gradeLevel}/{subject}")
    @Operation(summary = "Lấy tài liệu học tập theo lớp và môn học")
    public ResponseEntity<ApiResponse<Map<String, List<DocumentResponseDto>>>> getLessonMaterials(
            @PathVariable Integer gradeLevel,
            @PathVariable String subject) {
        try {
            List<DocumentResponseDto> documents = documentService.getDocumentsByGradeLevelAndSubject(gradeLevel, subject);

            // Phân loại tài liệu theo category
            Map<String, List<DocumentResponseDto>> categorizedDocuments = documents.stream()
                    .collect(Collectors.groupingBy(DocumentResponseDto::getCategory));

            return ResponseEntity.ok(ApiResponse.success("Lấy tài liệu học tập thành công", categorizedDocuments));
        } catch (Exception e) {
            log.error("Error getting lesson materials: ", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/curriculum/{gradeLevel}")
    @Operation(summary = "Lấy chương trình học theo lớp")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurriculum(@PathVariable Integer gradeLevel) {
        try {
            List<DocumentResponseDto> allDocuments = documentService.getDocumentsByGradeLevel(gradeLevel);

            // Phân loại theo môn học và loại tài liệu
            Map<String, Map<String, List<DocumentResponseDto>>> curriculum = new HashMap<>();

            for (DocumentResponseDto doc : allDocuments) {
                String subject = doc.getSubject();
                String category = doc.getCategory();

                curriculum.computeIfAbsent(subject, k -> new HashMap<>())
                         .computeIfAbsent(category, k -> new ArrayList<>())
                         .add(doc);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("gradeLevel", gradeLevel);
            result.put("totalDocuments", allDocuments.size());
            result.put("subjects", curriculum.keySet());
            result.put("curriculum", curriculum);
            result.put("statistics", createCurriculumStatistics(curriculum));

            return ResponseEntity.ok(ApiResponse.success("Lấy chương trình học thành công", result));
        } catch (Exception e) {
            log.error("Error getting curriculum: ", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/exercise-materials/{gradeLevel}/{subject}")
    @Operation(summary = "Lấy tài liệu bài tập theo lớp và môn học")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getExerciseMaterials(
            @PathVariable Integer gradeLevel,
            @PathVariable String subject) {
        try {
            // Lấy tài liệu loại "Bài tập"
            List<DocumentResponseDto> exercises = documentService.getDocumentsByGradeLevelAndSubject(gradeLevel, subject)
                    .stream()
                    .filter(doc -> "Bài tập".equals(doc.getCategory()) || "Đề kiểm tra".equals(doc.getCategory()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Lấy tài liệu bài tập thành công", exercises));
        } catch (Exception e) {
            log.error("Error getting exercise materials: ", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/teaching-materials/{gradeLevel}/{subject}")
    @Operation(summary = "Lấy tài liệu giảng dạy")
    public ResponseEntity<ApiResponse<Map<String, List<DocumentResponseDto>>>> getTeachingMaterials(
            @PathVariable Integer gradeLevel,
            @PathVariable String subject) {
        try {
            List<DocumentResponseDto> documents = documentService.getDocumentsByGradeLevelAndSubject(gradeLevel, subject);

            Map<String, List<DocumentResponseDto>> teachingMaterials = new HashMap<>();

            // Phân loại tài liệu dành cho giáo viên
            teachingMaterials.put("Giáo trình", filterByCategory(documents, "Giáo trình"));
            teachingMaterials.put("Hướng dẫn", filterByCategory(documents, "Hướng dẫn"));
            teachingMaterials.put("Video", filterByCategory(documents, "Video"));
            teachingMaterials.put("Audio", filterByCategory(documents, "Audio"));
            teachingMaterials.put("Tham khảo", filterByCategory(documents, "Tham khảo"));

            return ResponseEntity.ok(ApiResponse.success("Lấy tài liệu giảng dạy thành công", teachingMaterials));
        } catch (Exception e) {
            log.error("Error getting teaching materials: ", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/multimedia/{gradeLevel}/{subject}")
    @Operation(summary = "Lấy tài liệu đa phương tiện (video, audio)")
    public ResponseEntity<ApiResponse<Map<String, List<DocumentResponseDto>>>> getMultimediaMaterials(
            @PathVariable Integer gradeLevel,
            @PathVariable String subject) {
        try {
            List<DocumentResponseDto> documents = documentService.getDocumentsByGradeLevelAndSubject(gradeLevel, subject);

            Map<String, List<DocumentResponseDto>> multimedia = new HashMap<>();

            // Lọc theo loại file đa phương tiện
            multimedia.put("Video", documents.stream()
                    .filter(doc -> isVideoFile(doc.getFileType()))
                    .collect(Collectors.toList()));

            multimedia.put("Audio", documents.stream()
                    .filter(doc -> isAudioFile(doc.getFileType()))
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(ApiResponse.success("Lấy tài liệu đa phương tiện thành công", multimedia));
        } catch (Exception e) {
            log.error("Error getting multimedia materials: ", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/study-plan/{gradeLevel}")
    @Operation(summary = "Tạo kế hoạch học tập theo lớp")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createStudyPlan(@PathVariable Integer gradeLevel) {
        try {
            List<DocumentResponseDto> allDocuments = documentService.getDocumentsByGradeLevel(gradeLevel);

            Map<String, Object> studyPlan = new HashMap<>();
            studyPlan.put("gradeLevel", gradeLevel);
            studyPlan.put("totalDocuments", allDocuments.size());

            // Phân loại theo môn học
            Map<String, List<DocumentResponseDto>> subjectMaterials = allDocuments.stream()
                    .collect(Collectors.groupingBy(DocumentResponseDto::getSubject));

            Map<String, Map<String, Object>> subjectPlans = new HashMap<>();

            for (Map.Entry<String, List<DocumentResponseDto>> entry : subjectMaterials.entrySet()) {
                String subject = entry.getKey();
                List<DocumentResponseDto> documents = entry.getValue();

                Map<String, Object> subjectPlan = createSubjectStudyPlan(subject, documents);
                subjectPlans.put(subject, subjectPlan);
            }

            studyPlan.put("subjectPlans", subjectPlans);
            studyPlan.put("recommendations", createRecommendations(gradeLevel, allDocuments));

            return ResponseEntity.ok(ApiResponse.success("Tạo kế hoạch học tập thành công", studyPlan));
        } catch (Exception e) {
            log.error("Error creating study plan: ", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/popular-content/{gradeLevel}")
    @Operation(summary = "Lấy nội dung phổ biến theo lớp")
    public ResponseEntity<ApiResponse<Map<String, List<DocumentResponseDto>>>> getPopularContent(@PathVariable Integer gradeLevel) {
        try {
            List<DocumentResponseDto> documents = documentService.getDocumentsByGradeLevel(gradeLevel);

            Map<String, List<DocumentResponseDto>> popularContent = new HashMap<>();

            // Sắp xếp theo lượt xem
            popularContent.put("mostViewed", documents.stream()
                    .sorted((a, b) -> b.getViewCount().compareTo(a.getViewCount()))
                    .limit(10)
                    .collect(Collectors.toList()));

            // Sắp xếp theo lượt tải
            popularContent.put("mostDownloaded", documents.stream()
                    .sorted((a, b) -> b.getDownloadCount().compareTo(a.getDownloadCount()))
                    .limit(10)
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(ApiResponse.success("Lấy nội dung phổ biến thành công", popularContent));
        } catch (Exception e) {
            log.error("Error getting popular content: ", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    private List<DocumentResponseDto> filterByCategory(List<DocumentResponseDto> documents, String category) {
        return documents.stream()
                .filter(doc -> category.equals(doc.getCategory()))
                .collect(Collectors.toList());
    }

    private boolean isVideoFile(String fileType) {
        return Arrays.asList("MP4", "AVI", "MOV", "WEBM", "MKV").contains(fileType.toUpperCase());
    }

    private boolean isAudioFile(String fileType) {
        return Arrays.asList("MP3", "WAV", "M4A", "OGG", "FLAC").contains(fileType.toUpperCase());
    }

    private Map<String, Object> createCurriculumStatistics(Map<String, Map<String, List<DocumentResponseDto>>> curriculum) {
        Map<String, Object> stats = new HashMap<>();

        int totalSubjects = curriculum.size();
        int totalCategories = curriculum.values().stream()
                .mapToInt(Map::size)
                .sum();

        stats.put("totalSubjects", totalSubjects);
        stats.put("totalCategories", totalCategories);
        stats.put("subjectBreakdown", curriculum.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().values().stream().mapToInt(List::size).sum()
                )));

        return stats;
    }

    private Map<String, Object> createSubjectStudyPlan(String subject, List<DocumentResponseDto> documents) {
        Map<String, Object> plan = new HashMap<>();
        plan.put("subject", subject);
        plan.put("totalDocuments", documents.size());

        // Phân loại theo category
        Map<String, List<DocumentResponseDto>> categorized = documents.stream()
                .collect(Collectors.groupingBy(DocumentResponseDto::getCategory));

        plan.put("categories", categorized);
        plan.put("studySequence", createStudySequence(categorized));
        plan.put("estimatedHours", estimateStudyHours(documents));

        return plan;
    }

    private List<String> createStudySequence(Map<String, List<DocumentResponseDto>> categorized) {
        List<String> sequence = new ArrayList<>();

        // Thứ tự học tập đề xuất
        String[] preferredOrder = {"Giáo trình", "Hướng dẫn", "Video", "Audio", "Bài tập", "Đề kiểm tra", "Tham khảo"};

        for (String category : preferredOrder) {
            if (categorized.containsKey(category) && !categorized.get(category).isEmpty()) {
                sequence.add(category);
            }
        }

        return sequence;
    }

    private int estimateStudyHours(List<DocumentResponseDto> documents) {
        // Ước tính thời gian học dựa trên loại và số lượng tài liệu
        int hours = 0;

        for (DocumentResponseDto doc : documents) {
            switch (doc.getCategory()) {
                case "Giáo trình":
                    hours += 2; // 2 giờ cho mỗi giáo trình
                    break;
                case "Video":
                    if (doc.getDurationSeconds() != null) {
                        hours += doc.getDurationSeconds() / 3600 + 1; // thời lượng video + 1 giờ thảo luận
                    } else {
                        hours += 1;
                    }
                    break;
                case "Bài tập":
                    hours += 1; // 1 giờ cho mỗi bài tập
                    break;
                default:
                    hours += 1; // 30 phút cho các loại khác
                    break;
            }
        }

        return hours;
    }

    private List<String> createRecommendations(Integer gradeLevel, List<DocumentResponseDto> documents) {
        List<String> recommendations = new ArrayList<>();

        // Phân tích và đưa ra gợi ý
        Map<String, List<DocumentResponseDto>> bySubject = documents.stream()
                .collect(Collectors.groupingBy(DocumentResponseDto::getSubject));

        for (Map.Entry<String, List<DocumentResponseDto>> entry : bySubject.entrySet()) {
            String subject = entry.getKey();
            List<DocumentResponseDto> subjectDocs = entry.getValue();

            // Kiểm tra xem có đầy đủ loại tài liệu không
            Set<String> categories = subjectDocs.stream()
                    .map(DocumentResponseDto::getCategory)
                    .collect(Collectors.toSet());

            if (!categories.contains("Giáo trình")) {
                recommendations.add("Môn " + subject + " thiếu giáo trình cơ bản");
            }
            if (!categories.contains("Bài tập")) {
                recommendations.add("Môn " + subject + " thiếu bài tập thực hành");
            }
            if (!categories.contains("Video") && !categories.contains("Audio")) {
                recommendations.add("Môn " + subject + " thiếu tài liệu đa phương tiện");
            }
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Chương trình học đã đầy đủ các loại tài liệu cần thiết");
        }

        return recommendations;
    }
}
