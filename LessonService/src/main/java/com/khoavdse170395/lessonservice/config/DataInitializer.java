package com.khoavdse170395.lessonservice.config;

import com.khoavdse170395.lessonservice.entity.Lesson;
import com.khoavdse170395.lessonservice.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final LessonRepository lessonRepository;

    @Override
    public void run(String... args) throws Exception {
        // Chỉ khởi tạo dữ liệu nếu database trống
        if (lessonRepository.count() == 0) {
            initializeSampleLessons();
        }
    }

    private void initializeSampleLessons() {
        // Bài học lớp 6
        Lesson lesson1 = new Lesson(
            "Bài 1: Quê hương - Tố Hữu",
            "Phân tích bài thơ 'Quê hương' của nhà thơ Tố Hữu. Tìm hiểu về tình yêu quê hương trong thơ ca Việt Nam.",
            6,
            "Văn học",
            45,
            "Học sinh hiểu được tình cảm yêu quê hương của tác giả",
            "Thảo luận nhóm, phân tích từng đoạn thơ",
            "SGK Ngữ văn 6, bảng phụ, máy chiếu",
            "Học thuộc lòng đoạn thơ yêu thích"
        );

        Lesson lesson2 = new Lesson(
            "Bài 2: Luyện tập làm văn miêu tả",
            "Hướng dẫn học sinh viết đoạn văn miêu tả người thân trong gia đình",
            6,
            "Tập làm văn",
            90,
            "Học sinh biết cách miêu tả chân dung người",
            "Thực hành viết, sửa bài tập thể",
            "Giấy A4, bút viết, hình ảnh mẫu",
            "Hoàn thiện bài văn miêu tả"
        );

        // Bài học lớp 9
        Lesson lesson3 = new Lesson(
            "Bài 3: Chữ người tử tù - Nguyễn Tuân",
            "Phân tích tác phẩm 'Chữ người tử tù' - tìm hiểu về tinh thần yêu nước, ý chí kiên cường",
            9,
            "Văn học",
            45,
            "Hiểu được tinh thần yêu nước của nhân vật",
            "Phân tích tác phẩm, thảo luận",
            "SGK Ngữ văn 9, video tài liệu",
            "Viết cảm nhận về tác phẩm"
        );

        // Bài học lớp 12
        Lesson lesson4 = new Lesson(
            "Bài 4: Nghề viết văn - Maxim Gorky",
            "Tìm hiểu quan điểm về nghề viết văn của Maxim Gorky",
            12,
            "Văn học",
            45,
            "Hiểu được quan điểm của tác giả về nghề viết văn",
            "Seminar, thuyết trình nhóm",
            "Tài liệu tham khảo, máy tính",
            "Chuẩn bị bài thuyết trình"
        );

        // Bài học Tiếng Việt
        Lesson lesson5 = new Lesson(
            "Bài 5: Từ đồng âm, từ trái nghĩa",
            "Học về từ đồng âm và từ trái nghĩa trong tiếng Việt",
            7,
            "Tiếng Việt",
            45,
            "Phân biệt được từ đồng âm và từ trái nghĩa",
            "Bài tập thực hành, trò chơi học tập",
            "Bảng từ vựng, thẻ từ",
            "Làm bài tập SGK trang 25"
        );

        // Lưu các bài học mẫu
        lessonRepository.save(lesson1);
        lessonRepository.save(lesson2);
        lessonRepository.save(lesson3);
        lessonRepository.save(lesson4);
        lessonRepository.save(lesson5);

        System.out.println("Đã khởi tạo 5 bài học mẫu cho môn Ngữ văn");
    }
}
