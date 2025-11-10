package com.khoavdse170395.documentservice.service;

import com.khoavdse170395.documentservice.dto.DocumentRequestDto;
import com.khoavdse170395.documentservice.dto.DocumentResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface DocumentService {

    // CRUD operations
    List<DocumentResponseDto> getAllDocuments();

    Optional<DocumentResponseDto> getDocumentById(String id);

    DocumentResponseDto uploadDocument(MultipartFile file, DocumentRequestDto documentRequest, String uploadedBy);

    DocumentResponseDto updateDocument(String id, DocumentRequestDto documentRequest);

    boolean deleteDocument(String id);

    // Filter operations
    List<DocumentResponseDto> getDocumentsByCategory(String category);

    List<DocumentResponseDto> getDocumentsByGradeLevel(Integer gradeLevel);

    List<DocumentResponseDto> getDocumentsByFileType(String fileType);

    List<DocumentResponseDto> getDocumentsBySubject(String subject);

    List<DocumentResponseDto> getDocumentsByUser(String userId);

    List<DocumentResponseDto> getPublicDocuments();

    // Advanced filters
    List<DocumentResponseDto> getDocumentsByCategoryAndGradeLevel(String category, Integer gradeLevel);

    List<DocumentResponseDto> getDocumentsByCategoryAndFileType(String category, String fileType);

    List<DocumentResponseDto> getDocumentsByGradeLevelAndSubject(Integer gradeLevel, String subject);

    List<DocumentResponseDto> getDocumentsByCategoryAndGradeLevelAndFileType(String category, Integer gradeLevel, String fileType);

    // Search operations
    List<DocumentResponseDto> searchDocuments(String keyword);

    // Statistics
    List<DocumentResponseDto> getMostDownloadedDocuments();

    List<DocumentResponseDto> getMostViewedDocuments();

    // File operations
    void incrementDownloadCount(String documentId);

    void incrementViewCount(String documentId);

    String getFileUrl(String documentId);
}
