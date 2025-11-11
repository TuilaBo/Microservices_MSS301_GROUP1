package com.khoavdse170395.aiservice.controller;

import com.khoavdse170395.aiservice.dto.request.QuestionAnswerRequest;
import com.khoavdse170395.aiservice.dto.response.ApiResponse;
import com.khoavdse170395.aiservice.dto.response.GradingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/grading")
public class AIController {
    private final ChatClient chatClient;

    private final String prompt = """
                        You are an expert literature teacher grading Vietnamese essay (nghị luận) responses.
                        
                        Your task: evaluate the following student essay according to the official Vietnamese literature grading standards (thang điểm 10).
                        Please give feedback in Vietnamese
                        
                        Please follow this barem strictly:
                        
                        1. **Understanding of the prompt / thesis (2 points)** 
                           - 2.0: Nắm đúng yêu cầu đề, xác định đúng vấn đề nghị luận. 
                           - 1.0–1.5: Hiểu chưa đầy đủ hoặc hơi lệch yêu cầu. 
                           - 0–0.5: Lạc đề, không xác định được vấn đề.
                        
                        2. **Content and argument quality (4 points)** 
                           - 4.0: Triển khai đầy đủ các luận điểm chính, có dẫn chứng tiêu biểu, phân tích sâu sắc. \s
                           - 3.0–3.5: Có đủ các luận điểm, dẫn chứng hợp lý nhưng phân tích chưa sâu. 
                           - 1.5–2.5: Thiếu hoặc yếu ở một số luận điểm, dẫn chứng nghèo nàn. 
                           - 0–1.0: Lạc hướng, sai nội dung, không có lập luận.
                        
                        3. **Organization and coherence (2 points)** 
                           - 2.0: Bố cục 3 phần rõ ràng (mở – thân – kết), lập luận logic, mạch lạc. 
                           - 1.0–1.5: Bố cục tương đối rõ nhưng liên kết yếu. 
                           - 0–0.5: Bố cục rời rạc, thiếu liên kết.
                        
                        4. **Expression and language (1.5 points)** 
                           - 1.5: Diễn đạt trong sáng, giàu cảm xúc, ngữ pháp chuẩn. 
                           - 1.0: Diễn đạt tương đối tốt, còn lỗi nhỏ. 
                           - 0–0.5: Diễn đạt yếu, nhiều lỗi chính tả hoặc ngữ pháp.
                        
                        5. **Creativity and personal insight (0.5 points)** 
                           - 0.5: Có cách nhìn mới, dẫn chứng sáng tạo. 
                           - 0–0.25: Thiếu sáng tạo, máy móc hoặc sao chép.
                        
                        ---
                        
                        **Output format (JSON):**
                        ```json
                        {
                          "understanding": 1.5,
                          "contentQuality": 3.5,
                          "organization": 2,
                          "expression": 1.0,
                          "creativity": 0.25,
                          "total": 8.25,
                          "feedback": "Bài viết hiểu đúng đề, có dẫn chứng hợp lý nhưng phân tích chưa sâu. Cần chú ý diễn đạt và tăng tính sáng tạo."
                        }
                        Here's the question
                        <question>
                        """;


    //nhớ phải tạo QuestionAnswerRequest và GradingResponse + thêm api key vào file yaml nha
    @PostMapping
    public ApiResponse<GradingResponse> gradeFRQForLiteratureSubject(@RequestBody QuestionAnswerRequest questionAnswerRequest){
        var renderer = StTemplateRenderer.builder()
                .startDelimiterToken('<')
                .endDelimiterToken('>')
                .build();


        var response = chatClient.prompt()
                .templateRenderer(renderer)
                .system(s -> s.text(prompt).param("question", questionAnswerRequest.question()))
                .user(questionAnswerRequest.answer())
                .call()
                .entity(GradingResponse.class);
        System.out.println(response.feedback());
        return ApiResponse.success(response);

    }




}
