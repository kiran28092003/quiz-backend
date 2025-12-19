package com.example.quiz.service;

import com.example.quiz.model.Question;
import com.example.quiz.model.Score;
import com.example.quiz.repo.QuestionRepository;
import com.example.quiz.repo.ScoreRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

/**
 * QuizService — contains DB access helpers using MongoTemplate
 */
@Service
public class QuizService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    public List<String> getCategories() {
        return mongoTemplate.query(Question.class).distinct("category").as(String.class).all();
    }

    /**
     * Return N random questions for a category + difficulty.
     * Uses aggregation match -> sample -> project so only needed fields are returned.
     */
    public List<Question> getRandomQuestions(String category, String difficulty, int size) {
        Criteria c = Criteria.where("category").is(category).and("difficulty").is(difficulty);

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(c),
                Aggregation.sample(size),
                // project only fields the frontend needs (adjust names to match your Question model)
                Aggregation.project("question", "options").and("_id").as("id")
        );

        AggregationResults<Question> results = mongoTemplate.aggregate(agg, "questions", Question.class);
        return results.getMappedResults();
    }

    public Score saveScore(Score s) {
        if (s.getDate() == null) s.setDate(new java.util.Date());
        return scoreRepository.save(s);
    }

    /**
     * Return top scores but grouped by player name (one row per player).
     * We sort by score desc, date asc BEFORE grouping so the .first(...) after grouping
     * picks the best attempt for each player.
     */
    public List<Score> getTopScores(String category, String difficulty, int top) {
        // Match selected category + difficulty
        MatchOperation match = Aggregation.match(
                Criteria.where("category").is(category).and("difficulty").is(difficulty)
        );

        // Sort descending by score (best first), then by date ascending (older first when tie)
        SortOperation preGroupSort = Aggregation.sort(Sort.by(Direction.DESC, "score").and(Sort.by(Direction.ASC, "date")));

        // Group by player name — because we sorted before grouping, .first(...) will pick the best attempt
        GroupOperation groupByPlayer = Aggregation.group("name")
                .first("name").as("name")
                .first("score").as("score")
                .first("total").as("total")
                .first("date").as("date");

        // Now sort again by score desc and limit to top N
        SortOperation postSort = Aggregation.sort(Sort.by(Direction.DESC, "score"));
        LimitOperation limit = Aggregation.limit(top);

        // Build projection so mapping to Document/Score works
        ProjectionOperation project = Aggregation.project("name", "score", "total", "date");

        Aggregation agg = Aggregation.newAggregation(match, preGroupSort, groupByPlayer, postSort, limit, project);

        AggregationResults<Document> out = mongoTemplate.aggregate(agg, "scores", Document.class);
        List<Document> docs = out.getMappedResults();

        // Convert Documents to Score (simple mapping)
        List<Score> result = new ArrayList<>();
        for (Document d : docs) {
            Score s = new Score();
            s.setName(d.getString("name"));
            // Document#getInteger may return null depending on stored type; handle safely
            Object scoreObj = d.get("score");
            if (scoreObj instanceof Number) {
                s.setScore(((Number) scoreObj).intValue());
            } else {
                s.setScore(0);
            }
            Object totalObj = d.get("total");
            if (totalObj instanceof Number) {
                s.setTotal(((Number) totalObj).intValue());
            } else {
                s.setTotal(0);
            }
            s.setDate(d.getDate("date"));
            // set matched category/difficulty back on the DTO
            s.setCategory(category);
            s.setDifficulty(difficulty);
            result.add(s);
        }
        return result;
    }

    // You may keep your original repository-based methods as well if needed.
}
