package com.khoavdse170395.documentservice.config;

import com.khoavdse170395.documentservice.entity.DocumentEntity;
import com.khoavdse170395.documentservice.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final DocumentRepository documentRepository;
    private final Random random = new Random();

    @Override
    public void run(String... args) {
        try {
            long existingCount = documentRepository.count();
            log.info("📊 Current documents in database: {}", existingCount);

            if (existingCount == 0) {
                log.info("🔄 Starting Vietnamese Literature Document Database initialization...");

                initializeVietnameseLiterature();
                initializeLiteratureExercises();
                initializeLiteratureMedia();
                initializeLiteratureExams();

                long finalCount = documentRepository.count();
                log.info("✅ Data initialization completed! Total documents created: {}", finalCount);

                if (finalCount == 0) {
                    log.error("❌ WARNING: No documents were saved to database! Check MongoDB connection and entity mapping.");
                }
            } else {
                log.info("📚 Document database already contains {} documents, skipping initialization", existingCount);
            }
        } catch (Exception e) {
            log.error("💥 CRITICAL ERROR during data initialization: {}", e.getMessage(), e);
            log.error("Stack trace:", e);
        }
    }

    private void initializeVietnameseLiterature() {
        log.info("📖 Initializing Vietnamese Literature documents...");

        // Lớp 9 - Văn học dân gian và cổ điển
        createDocument("Văn học dân gian Việt Nam",
                "Tổng quan về văn học dân gian: ca dao, tục ngữ, thần thoại, truyền thuyết",
                "van_hoc_dan_gian_9.pdf", "PDF", "Giáo trình", 9, "Ngữ văn",
                "văn học dân gian,ca dao,tục ngữ,thần thoại", true);

        createDocument("Ca dao Việt Nam - Phân tích và hiểu nghĩa",
                "Hướng dẫn phân tích ca dao: nội dung, nghệ thuật, giá trị văn hóa",
                "ca_dao_phan_tich_9.pdf", "PDF", "Giáo trình", 9, "Ngữ văn",
                "ca dao,phân tích,văn hóa dân gian", true);

        createDocument("Tục ngữ và thành ngữ Việt Nam",
                "Bộ sưu tập tục ngữ, thành ngữ phổ biến và ý nghĩa",
                "tuc_ngu_thanh_ngu_9.docx", "DOCX", "Tài liệu", 9, "Ngữ văn",
                "tục ngữ,thành ngữ,ý nghĩa", true);

        // Lớp 10 - Văn học trung đại
        createDocument("Nguyễn Trãi và Bình Ngô đại cáo",
                "Phân tích tác phẩm Bình Ngô đại cáo - kiệt tác văn xuôi thế kỷ XV",
                "binh_ngo_dai_cao_10.pdf", "PDF", "Giáo trình", 10, "Ngữ văn",
                "Nguyễn Trãi,Bình Ngô đại cáo,văn xuôi cổ", true);

        createDocument("Nguyễn Bỉnh Khiêm - Tư tưởng và nghệ thuật",
                "Nghiên cứu tư tưởng nhân sinh quan và nghệ thuật thơ Nguyễn Bỉnh Khiêm",
                "nguyen_binh_khiem_10.pdf", "PDF", "Giáo trình", 10, "Ngữ văn",
                "Nguyễn Bỉnh Khiêm,triết lý,thơ cổ", true);

        createDocument("Chữ Nôm và văn học viết bằng chữ Nôm",
                "Tìm hiểu chữ Nôm và những tác phẩm văn học tiêu biểu",
                "chu_nom_van_hoc_10.docx", "DOCX", "Tài liệu", 10, "Ngữ văn",
                "chữ Nôm,văn học cổ,Việt Nam", true);

        // Lớp 11 - Văn học hiện thực thế kỷ XIX
        createDocument("Nguyễn Du và Truyện Kiều",
                "Phân tích toàn diện Truyện Kiều: nội dung, nghệ thuật, giá trị",
                "truyen_kieu_nguyen_du_11.pdf", "PDF", "Giáo trình", 11, "Ngữ văn",
                "Nguyễn Du,Truyện Kiều,hiện thực", true);

        createDocument("Hồ Xuân Hương - Thi ca và nhân cách",
                "Nghiên cứu thơ Hồ Xuân Hương: nghệ thuật và tinh thần phản kháng",
                "ho_xuan_huong_11.pdf", "PDF", "Giáo trình", 11, "Ngữ văn",
                "Hồ Xuân Hương,thơ,phản kháng", true);

        createDocument("Nguyễn Đình Chiểu và Lục Vân Tiên",
                "Tác phẩm Lục Vân Tiên: đặc sắc nghệ thuật và ý nghĩa nhân văn",
                "luc_van_tien_11.pdf", "PDF", "Giáo trình", 11, "Ngữ văn",
                "Nguyễn Đình Chiểu,Lục Vân Tiên,nhân văn", true);

        // Lớp 12 - Văn học hiện đại
        createDocument("Văn học Việt Nam 1930-1945",
                "Phong trào Thơ mới: Xuân Diệu, Huy Cận, Tế Hanh",
                "van_hoc_1930_1945_12.pdf", "PDF", "Giáo trình", 12, "Ngữ văn",
                "Thơ mới,Xuân Diệu,Huy Cận,Tế Hanh", true);

        createDocument("Hồ Chí Minh với văn học và báo chí",
                "Tác phẩm văn học và báo chí của Chủ tịch Hồ Chí Minh",
                "ho_chi_minh_van_hoc_12.pdf", "PDF", "Giáo trình", 12, "Ngữ văn",
                "Hồ Chí Minh,văn học,báo chí", true);

        createDocument("Tố Hữu - Thơ ca cách mạng",
                "Nghiên cứu thơ Tố Hữu: đặc sắc nghệ thuật và tinh thần cách mạng",
                "to_huu_tho_cach_mang_12.pdf", "PDF", "Giáo trình", 12, "Ngữ văn",
                "Tố Hữu,thơ cách mạng,kháng chiến", true);

        createDocument("Văn học miền Nam trước 1975",
                "Khảo sát văn học miền Nam: Nguyễn Minh Châu, Sơn Nam, Dương Tường",
                "van_hoc_mien_nam_12.pdf", "PDF", "Giáo trình", 12, "Ngữ văn",
                "văn học miền Nam,Nguyễn Minh Châu,Sơn Nam", true);
    }

    private void initializeLiteratureExercises() {
        log.info("📝 Initializing Literature Exercise documents...");

        // Bài tập lớp 9
        createDocument("Bài tập phân tích ca dao tục ngữ",
                "Tuyển tập bài tập và hướng dẫn phân tích ca dao, tục ngữ Việt Nam",
                "bai_tap_ca_dao_9.docx", "DOCX", "Bài tập", 9, "Ngữ văn",
                "bài tập,ca dao,phân tích", true);

        createDocument("Viết văn về văn học dân gian",
                "Hướng dẫn viết các dạng bài văn về chủ đề văn học dân gian",
                "viet_van_dan_gian_9.docx", "DOCX", "Bài tập", 9, "Ngữ văn",
                "viết văn,văn học dân gian,hướng dẫn", true);

        // Bài tập lớp 10
        createDocument("Phân tích tác phẩm văn xuôi cổ",
                "Bài tập phân tích Bình Ngô đại cáo và các tác phẩm văn xuôi thời Trung đại",
                "phan_tich_van_xui_co_10.docx", "DOCX", "Bài tập", 10, "Ngữ văn",
                "phân tích,văn xuôi cổ,Trung đại", true);

        createDocument("Làm văn nghị luận về nhân vật lịch sử",
                "Hướng dẫn viết bài nghị luận về các nhân vật lịch sử qua văn học",
                "nghi_luan_nhan_vat_10.docx", "DOCX", "Bài tập", 10, "Ng�� văn",
                "nghị luận,nhân vật lịch sử,viết văn", true);

        // Bài tập lớp 11
        createDocument("Phân tích nhân vật Thúy Kiều",
                "Bài tập chi tiết về hình tượng nhân vật Thúy Kiều trong Truyện Kiều",
                "phan_tich_thuy_kieu_11.docx", "DOCX", "Bài tập", 11, "Ngữ văn",
                "Thúy Kiều,nhân vật,phân tích", true);

        createDocument("So sánh thơ Nguyễn Du và Hồ Xuân Hương",
                "Bài tập so sánh nghệ thuật thơ ca của hai tác giả lớn thế kỷ XIX",
                "so_sanh_nguyen_du_ho_xuan_huong_11.docx", "DOCX", "Bài tập", 11, "Ngữ văn",
                "so sánh,Nguyễn Du,Hồ Xuân Hương", true);

        // Bài tập lớp 12
        createDocument("Phân tích tác phẩm Thơ mới",
                "Bài tập phân tích thơ Xuân Diệu, Huy Cận và các tác giả Thơ mới",
                "phan_tich_tho_moi_12.docx", "DOCX", "Bài tập", 12, "Ngữ văn",
                "Thơ mới,phân tích,hiện đại", true);

        createDocument("Viết văn nghị luận văn học",
                "Hướng dẫn viết bài nghị luận về các vấn đề văn học, tác giả, tác phẩm",
                "nghi_luan_van_hoc_12.docx", "DOCX", "Bài tập", 12, "Ngữ văn",
                "nghị luận,văn học,viết văn", true);
    }

    private void initializeLiteratureMedia() {
        log.info("🎬 Initializing Literature Media documents...");

        // Video bài giảng
        createDocument("Video: Phân tích ca dao Việt Nam",
                "Video hướng dẫn cách phân tích và hiểu ý nghĩa ca dao dân gian",
                "video_ca_dao_9.mp4", "MP4", "Video", 9, "Ngữ văn",
                "video,ca dao,phân tích,dân gian", true, 1800);

        createDocument("Video: Nguyễn Trãi và Bình Ngô đại cáo",
                "Video giảng về cuộc đời, sự nghiệp và tác phẩm Bình Ngô đại cáo",
                "video_nguyen_trai_10.mp4", "MP4", "Video", 10, "Ngữ văn",
                "video,Nguyễn Trãi,Bình Ngô đại cáo", true, 2400);

        createDocument("Video: Truyện Kiều - Tình yêu và số phận",
                "Video phân tích chủ đề tình yêu và số phận trong Truyện Kiều",
                "video_truyen_kieu_11.mp4", "MP4", "Video", 11, "Ngữ văn",
                "video,Truyện Kiều,tình yêu,số phận", true, 2700);

        createDocument("Video: Thơ Tố Hữu - Tinh thần cách mạng",
                "Video giảng về đặc sắc nghệ thuật và tinh thần trong thơ Tố Hữu",
                "video_to_huu_12.mp4", "MP4", "Video", 12, "Ngữ văn",
                "video,Tố Hữu,thơ,cách mạng", true, 3000);

        // Audio ngâm thơ
        createDocument("Audio: Ngâm thơ Nguyễn Du",
                "Bản ngâm thơ Truyện Kiều của nghệ sĩ nổi tiếng",
                "ngam_tho_nguyen_du_11.mp3", "MP3", "Audio", 11, "Ngữ văn",
                "audio,ngâm thơ,Nguyễn Du,Truyện Kiều", true, 2400);

        createDocument("Audio: Ngâm ca dao dân gian",
                "Tuyển tập các bản ngâm ca dao dân gian Việt Nam hay nhất",
                "ngam_ca_dao_9.mp3", "MP3", "Audio", 9, "Ngữ văn",
                "audio,ngâm thơ,ca dao,dân gian", true, 1800);

        createDocument("Audio: Thơ Hồ Xuân Hương",
                "Bản ngâm thơ Hồ Xuân Hương với giọng đọc truyền cảm",
                "ngam_ho_xuan_huong_11.mp3", "MP3", "Audio", 11, "Ngữ văn",
                "audio,ngâm thơ,Hồ Xuân Hương", true, 2100);
    }

    private void initializeLiteratureExams() {
        log.info("📋 Initializing Literature Exam documents...");

        // Đề kiểm tra lớp 9
        createDocument("Đề kiểm tra văn học dân gian - Lớp 9",
                "Đề kiểm tra 15 phút môn Ngữ văn chủ đề văn học dân gian",
                "de_kiem_tra_dan_gian_9.pdf", "PDF", "Đề kiểm tra", 9, "Ngữ văn",
                "đề kiểm tra,văn học dân gian,15 phút", true);

        createDocument("Đề kiểm tra giữa học kỳ I - Lớp 9",
                "Đề kiểm tra giữa học kỳ I môn Ngữ văn lớp 9",
                "de_kiem_tra_giua_ki_1_9.pdf", "PDF", "Đề kiểm tra", 9, "Ngữ văn",
                "đề kiểm tra,giữa kỳ,lớp 9", true);

        // Đề kiểm tra lớp 10
        createDocument("Đề kiểm tra văn học trung đại - Lớp 10",
                "Đề kiểm tra chuyên đề văn học trung đại Việt Nam",
                "de_kiem_tra_trung_dai_10.pdf", "PDF", "Đề kiểm tra", 10, "Ngữ văn",
                "đề kiểm tra,văn học trung đại,lớp 10", true);

        createDocument("Đề thi học kỳ II - Lớp 10",
                "Đề thi học kỳ II môn Ngữ văn lớp 10 có đáp án chi tiết",
                "de_thi_hoc_ky_2_10.pdf", "PDF", "Đề kiểm tra", 10, "Ngữ văn",
                "đề thi,học kỳ,lớp 10,có đáp án", true);

        // Đề kiểm tra lớp 11
        createDocument("Đề kiểm tra Truyện Kiều - Lớp 11",
                "Đề kiểm tra chuyên sâu về tác phẩm Truyện Kiều",
                "de_kiem_tra_truyen_kieu_11.pdf", "PDF", "Đề kiểm tra", 11, "Ngữ văn",
                "đề kiểm tra,Truyện Kiều,chuyên sâu", true);

        createDocument("Đề thi thử THPT Quốc gia - Lớp 11",
                "Đề thi thử THPT Quốc gia môn Ngữ văn cho học sinh lớp 11",
                "de_thi_thu_thpt_11.pdf", "PDF", "Đề kiểm tra", 11, "Ngữ văn",
                "đề thi thử,THPT Quốc gia,lớp 11", true);

        // Đề kiểm tra lớp 12
        createDocument("Đề thi thử THPT Quốc gia 2024 - Ngữ văn",
                "Bộ đề thi thử THPT Quốc gia môn Ngữ văn năm 2024",
                "de_thi_thu_thpt_2024.pdf", "PDF", "Đề kiểm tra", 12, "Ngữ văn",
                "đề thi thử,THPT Quốc gia,2024", true);

        createDocument("Đề minh họa THPT Quốc gia - Ngữ văn",
                "Đề minh họa kỳ thi THPT Quốc gia môn Ngữ văn của Bộ GD&ĐT",
                "de_minh_hoa_thpt.pdf", "PDF", "Đề kiểm tra", 12, "Ngữ văn",
                "đề minh họa,THPT,Bộ GD&ĐT", true);
    }

    private void createDocument(String title, String description, String fileName,
                               String fileType, String category, Integer gradeLevel, String subject,
                               String tags, Boolean isPublic) {
        createDocument(title, description, fileName, fileType, category, gradeLevel, subject, tags, isPublic, null);
    }

    private void createDocument(String title, String description, String fileName,
                               String fileType, String category, Integer gradeLevel, String subject,
                               String tags, Boolean isPublic, Integer durationSeconds) {

        try {
            // Generate realistic file size
            long fileSize = generateFileSize(fileType);

            DocumentEntity document = DocumentEntity.builder()
                    .title(title)
                    .description(description)
                    .fileName(fileName)
                    .fileType(fileType)
                    .fileSize(fileSize)
                    .fileUrl("/uploads/documents/" + fileName)
                    .thumbnailUrl(generateThumbnailUrl(fileType))
                    .category(category)
                    .gradeLevel(gradeLevel)
                    .subject(subject)
                    .tags(tags)
                    .isPublic(isPublic)
                    .isActive(true)
                    .uploadedBy("admin")
                    .viewCount(random.nextInt(500) + 50)
                    .downloadCount(random.nextInt(200) + 10)
                    .durationSeconds(durationSeconds)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            DocumentEntity saved = documentRepository.save(document);
            log.debug("✅ Created document: {} (ID: {})", title, saved.getId());

        } catch (Exception e) {
            log.error("❌ Failed to create document '{}': {}", title, e.getMessage(), e);
        }
    }

    private long generateFileSize(String fileType) {
        return switch (fileType.toLowerCase()) {
            case "pdf" -> 1_000_000L + random.nextInt(5_000_000); // 1-6MB
            case "docx", "doc" -> 500_000L + random.nextInt(2_000_000); // 0.5-2.5MB
            case "mp4" -> 50_000_000L + random.nextInt(100_000_000); // 50-150MB
            case "mp3" -> 5_000_000L + random.nextInt(10_000_000); // 5-15MB
            default -> 1_000_000L + random.nextInt(3_000_000); // 1-4MB
        };
    }

    private String generateThumbnailUrl(String fileType) {
        return switch (fileType.toLowerCase()) {
            case "pdf" -> "/uploads/thumbnails/pdf-icon.png";
            case "docx", "doc" -> "/uploads/thumbnails/word-icon.png";
            case "mp4" -> "/uploads/thumbnails/video-icon.png";
            case "mp3" -> "/uploads/thumbnails/audio-icon.png";
            default -> "/uploads/thumbnails/file-icon.png";
        };
    }
}
