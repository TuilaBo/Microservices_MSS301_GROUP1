package com.khoavdse170395.documentservice.service.impl;

import com.khoavdse170395.documentservice.dto.DocumentRequestDto;
import com.khoavdse170395.documentservice.dto.DocumentResponseDto;
import com.khoavdse170395.documentservice.entity.DocumentEntity;
import com.khoavdse170395.documentservice.repository.DocumentRepository;
import com.khoavdse170395.documentservice.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final Tika tika = new Tika();

    // Thư mục lưu trữ file
    private static final String UPLOAD_DIR = "uploads/documents/";

    @Override
    public List<DocumentResponseDto> getAllDocuments() {
        return documentRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DocumentResponseDto> getDocumentById(String id) {
        return documentRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public DocumentResponseDto uploadDocument(MultipartFile file, DocumentRequestDto documentRequest, String uploadedBy) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File không được để trống");
            }

            // Detect file type
            String contentType = tika.detect(file.getBytes());
            String fileExtension = getFileExtension(file.getOriginalFilename());

            // Generate unique filename
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Create directory if not exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save file
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // Create document entity
            DocumentEntity document = DocumentEntity.builder()
                    .title(documentRequest.getTitle())
                    .description(documentRequest.getDescription())
                    .fileName(fileName)
                    .fileType(fileExtension.toUpperCase())
                    .fileUrl(filePath.toString())
                    .fileSize(file.getSize())
                    .category(documentRequest.getCategory())
                    .gradeLevel(documentRequest.getGradeLevel())
                    .subject(documentRequest.getSubject())
                    .uploadedBy(uploadedBy)
                    .downloadCount(0)
                    .viewCount(0)
                    .tags(documentRequest.getTags())
                    .isPublic(documentRequest.getIsPublic())
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            DocumentEntity savedDocument = documentRepository.save(document);
            return convertToDto(savedDocument);

        } catch (IOException e) {
            log.error("Error uploading file: ", e);
            throw new RuntimeException("Lỗi khi upload file: " + e.getMessage());
        }
    }

    @Override
    public DocumentResponseDto updateDocument(String id, DocumentRequestDto documentRequest) {
        Optional<DocumentEntity> optionalDocument = documentRepository.findById(id);
        if (optionalDocument.isPresent()) {
            DocumentEntity document = optionalDocument.get();
            document.setTitle(documentRequest.getTitle());
            document.setDescription(documentRequest.getDescription());
            document.setCategory(documentRequest.getCategory());
            document.setGradeLevel(documentRequest.getGradeLevel());
            document.setSubject(documentRequest.getSubject());
            document.setTags(documentRequest.getTags());
            document.setIsPublic(documentRequest.getIsPublic());
            document.setUpdatedAt(LocalDateTime.now());

            DocumentEntity savedDocument = documentRepository.save(document);
            return convertToDto(savedDocument);
        }
        throw new RuntimeException("Không tìm thấy document với ID: " + id);
    }

    @Override
    public boolean deleteDocument(String id) {
        Optional<DocumentEntity> optionalDocument = documentRepository.findById(id);
        if (optionalDocument.isPresent()) {
            DocumentEntity document = optionalDocument.get();
            // Soft delete
            document.setIsActive(false);
            document.setUpdatedAt(LocalDateTime.now());
            documentRepository.save(document);

            // Optionally delete physical file
            try {
                Path filePath = Paths.get(document.getFileUrl());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.warn("Could not delete file: " + document.getFileUrl(), e);
            }

            return true;
        }
        return false;
    }

    @Override
    public List<DocumentResponseDto> getDocumentsByCategory(String category) {
        return documentRepository.findByCategory(category)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDto> getDocumentsByGradeLevel(Integer gradeLevel) {
        return documentRepository.findByGradeLevel(gradeLevel)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDto> getDocumentsByFileType(String fileType) {
        return documentRepository.findByFileType(fileType.toUpperCase())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDto> getDocumentsBySubject(String subject) {
        return documentRepository.findBySubject(subject)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDto> getDocumentsByUser(String userId) {
        return documentRepository.findByUploadedBy(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDto> getPublicDocuments() {
        return documentRepository.findByIsPublicTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDto> getDocumentsByCategoryAndGradeLevel(String category, Integer gradeLevel) {
        return documentRepository.findByCategoryAndGradeLevel(category, gradeLevel)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDto> getDocumentsByCategoryAndFileType(String category, String fileType) {
        return documentRepository.findByCategoryAndFileType(category, fileType.toUpperCase())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDto> getDocumentsByGradeLevelAndSubject(Integer gradeLevel, String subject) {
        return documentRepository.findByGradeLevelAndSubject(gradeLevel, subject)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDto> getDocumentsByCategoryAndGradeLevelAndFileType(String category, Integer gradeLevel, String fileType) {
        return documentRepository.findByCategoryAndGradeLevelAndFileType(category, gradeLevel, fileType.toUpperCase())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDto> searchDocuments(String keyword) {
        return documentRepository.findByTitleOrDescriptionOrTagsContaining(keyword)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDto> getMostDownloadedDocuments() {
        return documentRepository.findTop10ByOrderByDownloadCountDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDto> getMostViewedDocuments() {
        return documentRepository.findTop10ByOrderByViewCountDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void incrementDownloadCount(String documentId) {
        Optional<DocumentEntity> optionalDocument = documentRepository.findById(documentId);
        if (optionalDocument.isPresent()) {
            DocumentEntity document = optionalDocument.get();
            document.setDownloadCount(document.getDownloadCount() + 1);
            document.setUpdatedAt(LocalDateTime.now());
            documentRepository.save(document);
        }
    }

    @Override
    public void incrementViewCount(String documentId) {
        Optional<DocumentEntity> optionalDocument = documentRepository.findById(documentId);
        if (optionalDocument.isPresent()) {
            DocumentEntity document = optionalDocument.get();
            document.setViewCount(document.getViewCount() + 1);
            document.setUpdatedAt(LocalDateTime.now());
            documentRepository.save(document);
        }
    }

    @Override
    public String getFileUrl(String documentId) {
        return documentRepository.findById(documentId)
                .map(DocumentEntity::getFileUrl)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
    }

    private DocumentResponseDto convertToDto(DocumentEntity entity) {
        DocumentResponseDto dto = new DocumentResponseDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
