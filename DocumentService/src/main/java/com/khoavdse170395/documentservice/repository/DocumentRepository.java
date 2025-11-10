package com.khoavdse170395.documentservice.repository;

import com.khoavdse170395.documentservice.entity.DocumentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<DocumentEntity, String> {

    List<DocumentEntity> findByCategory(String category);

    List<DocumentEntity> findByGradeLevel(Integer gradeLevel);

    List<DocumentEntity> findByFileType(String fileType);

    List<DocumentEntity> findBySubject(String subject);

    List<DocumentEntity> findByUploadedBy(String uploadedBy);

    List<DocumentEntity> findByIsPublicTrue();

    List<DocumentEntity> findByIsActiveTrue();

    List<DocumentEntity> findByCategoryAndGradeLevel(String category, Integer gradeLevel);

    List<DocumentEntity> findByCategoryAndFileType(String category, String fileType);

    List<DocumentEntity> findByGradeLevelAndSubject(Integer gradeLevel, String subject);

    @Query("{ $or: [ " +
           "{ 'title': { $regex: ?0, $options: 'i' } }, " +
           "{ 'description': { $regex: ?0, $options: 'i' } }, " +
           "{ 'tags': { $regex: ?0, $options: 'i' } } " +
           "] }")
    List<DocumentEntity> findByTitleOrDescriptionOrTagsContaining(String keyword);

    @Query("{ 'category': ?0, 'gradeLevel': ?1, 'fileType': ?2, 'isActive': true }")
    List<DocumentEntity> findByCategoryAndGradeLevelAndFileType(String category, Integer gradeLevel, String fileType);

    List<DocumentEntity> findTop10ByOrderByDownloadCountDesc();

    List<DocumentEntity> findTop10ByOrderByViewCountDesc();

    // Count methods for statistics
    long countByCategory(String category);

    long countBySubject(String subject);

    long countByGradeLevel(Integer gradeLevel);
}
