package com.example.quiz.controller;

import com.example.quiz.dto.QuestionResponse;
import com.example.quiz.model.Question;
import com.example.quiz.model.Score;
import com.example.quiz.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*") // allow frontend during dev
@RestController
@RequestMapping("/api")
public class QuizController {
    @Autowired
    private QuizService quizService;

    @GetMapping("/categories")
    public List<String> categories() {
        return quizService.getCategories();
    }

    @GetMapping("/questions")
    public List<QuestionResponse> questions(
            @RequestParam String category,
            @RequestParam String difficulty,
            @RequestParam(defaultValue = "5") int size) {

        List<Question> qs = quizService.getRandomQuestions(category, difficulty, size);
        return qs.stream()
                .map(q -> new QuestionResponse(q.getId(), q.getQuestion(), q.getOptions()))
                .collect(Collectors.toList());
    }

    @PostMapping("/submit")
    public Score submit(@RequestBody Score score) {
        return quizService.saveScore(score);
    }

    @GetMapping("/leaderboard")
    public List<Score> leaderboard(@RequestParam String category,
                                   @RequestParam String difficulty,
                                   @RequestParam(defaultValue = "5") int top) {
        return quizService.getTopScores(category, difficulty, top);
    }
}
