package com.khoavdse170395.aiservice.configuration;

import com.khoavdse170395.aiservice.controller.AIController;
import com.khoavdse170395.aiservice.dto.request.QuestionAnswerRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

//    @Bean
//    ApplicationRunner applicationRunner(AIController aiController){
//        return args -> {
//            QuestionAnswerRequest questionAnswerRequest = new QuestionAnswerRequest("Phân tích vẻ đẹp của hình tượng người lính trong bài thơ \"Đồng chí\" của Chính Hữu.",
//                    "Bài thơ \"Đồng chí\" của Chính Hữu là một trong những tác phẩm tiêu biểu viết về người lính trong thời kỳ kháng chiến chống Pháp. Qua ngòi bút giản dị và chân thực, tác giả đã khắc họa hình tượng người lính xuất thân từ những người nông dân nghèo, họ gặp nhau trong hoàn cảnh chiến đấu gian khổ và trở thành đồng chí, đồng đội.\n" +
//                            "\n" +
//                            "Mở đầu bài thơ là những câu thơ ngắn gọn, mộc mạc:\n" +
//                            "“Quê hương anh nước mặn đồng chua\n" +
//                            "Làng tôi nghèo đất cày lên sỏi đá.”\n" +
//                            "Hai câu thơ cho thấy người lính đến từ nhiều miền quê khác nhau, nhưng họ có chung hoàn cảnh nghèo khó, chung lý tưởng chiến đấu. Chính sự đồng cảm ấy đã giúp họ gắn bó và thấu hiểu nhau.\n" +
//                            "\n" +
//                            "Tình đồng chí được hình thành từ sự chia sẻ gian lao, từ những đêm rét buốt trong rừng, từ “áo anh rách vai”, “quần tôi có vài mảnh vá”. Tác giả dùng hình ảnh chân thực để thể hiện tình cảm thiêng liêng, giản dị mà sâu sắc của người lính.\n" +
//                            "Đỉnh cao của bài thơ là hình ảnh “Đầu súng trăng treo” – biểu tượng đẹp đẽ cho tâm hồn lãng mạn và tinh thần lạc quan của người lính. Dù sống giữa chiến trường khốc liệt, họ vẫn giữ trong tim niềm tin và tình yêu cuộc sống.\n" +
//                            "\n" +
//                            "Bài thơ có ngôn ngữ giản dị, cảm xúc chân thành, giàu hình ảnh và nhạc điệu. Chính Hữu không miêu tả người lính như những anh hùng phi thường, mà như những con người bình dị, gần gũi, nhưng mang trong mình sức mạnh của tình đồng chí, đồng đội và lòng yêu nước.\n" +
//                            "\n" +
//                            "Qua \"Đồng chí\", Chính Hữu đã giúp người đọc hiểu hơn vẻ đẹp của người lính cách mạng Việt Nam: bình dị mà cao quý, gian khổ mà lạc quan, giản đơn mà sâu sắc.");
//
//            aiController.gradeFRQForLiteratureSubject(questionAnswerRequest);
//        };
//    }


    @Bean("ChatClientWithoutMemory")
    ChatClient chatClientWithoutMemory(ChatClient.Builder chatClientBuilder){
        return chatClientBuilder
                .build();

    }


}
