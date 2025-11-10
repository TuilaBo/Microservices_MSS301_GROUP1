package com.khoavdse170395.questionservice.config;

import com.khoavdse170395.questionservice.model.MockOption;
import com.khoavdse170395.questionservice.model.MockQuestion;
import com.khoavdse170395.questionservice.model.MockTest;
import com.khoavdse170395.questionservice.model.MembershipTier;
import com.khoavdse170395.questionservice.model.QuestionType;
import com.khoavdse170395.questionservice.repository.MockQuestionRepository;
import com.khoavdse170395.questionservice.repository.MockTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MockTestRepository mockTestRepository;
    private final MockQuestionRepository mockQuestionRepository;

    @Override
    public void run(String... args) {
        if (mockTestRepository.count() > 0) {
            return; // Skip seeding if data already exists
        }

        for (int t = 1; t <= 3; t++) {
            MockTest test = new MockTest();
            test.setName("Sample Test " + t);
            test.setDuration(Duration.ofMinutes(30));
            test.setTotalPoint(30);
            test.setLessonId(null);
            test.setRequiredTier(MembershipTier.BASIC);

            // Persist test first to obtain ID
            test = mockTestRepository.save(test);

            List<MockQuestion> questions = new ArrayList<>();
            for (int q = 1; q <= 3; q++) {
                MockQuestion question = new MockQuestion();
                question.setQuestion("Question " + q + " for Test " + t);
                question.setPoint(10);
                question.setQuestionType(QuestionType.MULTIPLE_CHOICES);
                question.setTest(test);

                List<MockOption> options = new ArrayList<>();
                for (int o = 1; o <= 4; o++) {
                    MockOption option = new MockOption();
                    option.setName("Option " + o);
                    option.setAnswer(o == 1); // mark first option as correct
                    option.setQuestion(question);
                    options.add(option);
                    if (option.isAnswer()) {
                        question.setAnswer(option);
                    }
                }
                question.setOptions(options);

                // Save question (cascades persist options)
                question = mockQuestionRepository.save(question);
                questions.add(question);
            }

            // Attach questions to test and update join table mapping
            test.setQuestions(questions);
            mockTestRepository.save(test);
        }
    }
}
