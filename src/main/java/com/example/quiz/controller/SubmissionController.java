package com.example.quiz.controller;

import com.example.quiz.dto.AnswerDTO;
import com.example.quiz.dto.SubmitAnswersRequest;
import com.example.quiz.model.Question;
import com.example.quiz.model.Score;
import com.example.quiz.service.QuizService;
import com.example.quiz.repo.QuestionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class SubmissionController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizService quizService;

    @PostMapping("/submitAnswers")
    public ResponseEntity<Map<String, Object>> submitAnswers(@RequestBody SubmitAnswersRequest req) {
        Map<String, Object> resp = new HashMap<>();

        // log request for debugging
        System.out.println(">>> /api/submitAnswers called. Payload: " + req);

        if (req == null) {
            resp.put("message", "Invalid request: body missing");
            return ResponseEntity.badRequest().body(resp);
        }

        List<AnswerDTO> answers = req.getAnswers() != null ? req.getAnswers() : Collections.emptyList();
        if (answers.isEmpty()) {
            resp.put("score", 0);
            resp.put("total", 0);
            resp.put("message", "No answers submitted");
            return ResponseEntity.badRequest().body(resp);
        }

        // fetch correct answers from DB for the provided question IDs
        List<String> ids = answers.stream()
                .map(AnswerDTO::getQuestionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        System.out.println("Question IDs received: " + ids);

        List<Question> questions = questionRepository.findAllById(ids);
        Map<String, String> correctMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, Question::getAnswer));

        int score = 0;
        for (AnswerDTO a : answers) {
            String correct = correctMap.get(a.getQuestionId());
            if (correct != null && correct.equals(a.getSelectedOption())) score++;
        }

        // save Score document (reuse existing Score model)
        Score s = new Score();
        s.setName(req.getName());
        s.setCategory(req.getCategory());
        s.setDifficulty(req.getDifficulty());
        s.setScore(score);
        s.setTotal(answers.size());
        s.setDate(new Date());

        Score saved = quizService.saveScore(s);

        resp.put("score", score);
        resp.put("total", answers.size());
        resp.put("saved", saved);
        resp.put("message", "Scored and saved");
        System.out.println("Score saved successfully: " + saved);

        return ResponseEntity.ok(resp);
    }
}
