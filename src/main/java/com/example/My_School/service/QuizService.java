package com.example.My_School.service;

import com.example.My_School.dto.*;
import com.example.My_School.model.*;
import com.example.My_School.repository.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

        private final QuizRepository quizRepo;
        private final QuizAttemptRepository attemptRepo;
        private final QuizAttemptAnswerRepository answerRepo;
        private final StudentRepository studentRepo;
        private final SchoolClassRepository classRepo;
        private final UserRepository userRepo;

        // -------------------------------------------------------
        // STAFF: Create quiz with questions
        // -------------------------------------------------------
        @Transactional
        public QuizDto createQuiz(QuizCreateDto dto, Long staffUserId) {
                SchoolClass cls = classRepo.findById(dto.getClassId())
                                .orElseThrow(() -> new RuntimeException("Class not found"));
                User staff = userRepo.findById(staffUserId).orElse(null);

                Quiz quiz = Quiz.builder()
                                .title(dto.getTitle())
                                .description(dto.getDescription())
                                .schoolClass(cls)
                                .subject(Quiz.Subject.valueOf(dto.getSubject()))
                                .durationMins(dto.getDurationMins())
                                .totalMarks(dto.getTotalMarks())
                                .passingMarks(dto.getPassingMarks())
                                .scheduledAt(dto.getScheduledAt())
                                .expiresAt(dto.getExpiresAt())
                                .status(dto.getStatus() != null ? Quiz.QuizStatus.valueOf(dto.getStatus())
                                                : Quiz.QuizStatus.DRAFT)
                                .createdBy(staff)
                                .build();

                quiz = quizRepo.save(quiz);

                if (dto.getQuestions() != null) {
                        int order = 1;
                        for (QuizCreateDto.QuestionInput q : dto.getQuestions()) {
                                QuizQuestion qq = QuizQuestion.builder()
                                                .quiz(quiz)
                                                .questionText(q.getQuestionText())
                                                .optionA(q.getOptionA())
                                                .optionB(q.getOptionB())
                                                .optionC(q.getOptionC())
                                                .optionD(q.getOptionD())
                                                .correctOption(QuizQuestion.Option.valueOf(q.getCorrectOption()))
                                                .marks(q.getMarks() > 0 ? q.getMarks() : 1)
                                                .questionOrder(order++)
                                                .build();
                                // Save via cascade by adding to quiz questions list
                                quiz.getQuestions(); // init
                        }
                        // Re-save with questions attached via separate saves for simplicity
                        quizRepo.save(quiz);
                }
                return toDto(quiz, null);
        }

        @Transactional
        public QuizDto createQuizWithQuestions(QuizCreateDto dto, Long staffUserId) {

                SchoolClass cls = classRepo.findById(dto.getClassId())
                                .orElseThrow(() -> new RuntimeException("Class not found"));

                User staff = null;
                if (staffUserId != null) {
                        staff = userRepo.findById(staffUserId)
                                        .orElseThrow(() -> new RuntimeException("Staff not found"));
                }

                // Create quiz
                Quiz quiz = new Quiz();
                quiz.setTitle(dto.getTitle());
                quiz.setDescription(dto.getDescription());
                quiz.setSchoolClass(cls);

                quiz.setSubject(
                                Quiz.Subject.valueOf(
                                                dto.getSubject().toUpperCase()));

                quiz.setDurationMins(dto.getDurationMins());
                quiz.setTotalMarks(dto.getTotalMarks());
                quiz.setPassingMarks(dto.getPassingMarks());
                quiz.setScheduledAt(dto.getScheduledAt());
                quiz.setExpiresAt(dto.getExpiresAt());
                quiz.setCreatedBy(staff);

                quiz.setStatus(
                                dto.getStatus() != null
                                                ? Quiz.QuizStatus.valueOf(
                                                                dto.getStatus().toUpperCase())
                                                : Quiz.QuizStatus.DRAFT);

                // Initialize question list
                List<QuizQuestion> questions = new ArrayList<>();

                if (dto.getQuestions() != null) {

                        int order = 1;

                        for (QuizCreateDto.QuestionInput qi : dto.getQuestions()) {

                                QuizQuestion q = new QuizQuestion();

                                q.setQuiz(quiz);

                                q.setQuestionText(qi.getQuestionText());

                                q.setOptionA(qi.getOptionA());
                                q.setOptionB(qi.getOptionB());
                                q.setOptionC(qi.getOptionC());
                                q.setOptionD(qi.getOptionD());

                                q.setCorrectOption(
                                                QuizQuestion.Option.valueOf(
                                                                qi.getCorrectOption().toUpperCase()));

                                q.setMarks(
                                                qi.getMarks() > 0
                                                                ? qi.getMarks()
                                                                : 1);

                                q.setQuestionOrder(order++);

                                questions.add(q);
                        }
                }

                // Attach questions to quiz
                quiz.setQuestions(questions);

                // Save once (cascade saves questions automatically)
                quiz = quizRepo.save(quiz);

                return toDto(quiz, null);
        }

        // -------------------------------------------------------
        // STAFF: Get all quizzes for a class (with question count)
        // -------------------------------------------------------
        public List<QuizDto> getQuizzesByClass(Long classId) {
                return quizRepo.findBySchoolClassIdOrderByCreatedAtDesc(classId)
                                .stream().map(q -> toDto(q, null)).collect(Collectors.toList());
        }

        // -------------------------------------------------------
        // STAFF: Update quiz status
        // -------------------------------------------------------
        @Transactional
        public QuizDto updateStatus(Long quizId, String status) {
                Quiz quiz = quizRepo.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
                quiz.setStatus(Quiz.QuizStatus.valueOf(status.toUpperCase()));
                return toDto(quizRepo.save(quiz), null);
        }

        // -------------------------------------------------------
        // STAFF: Get attempt results for a quiz (all students)
        // -------------------------------------------------------
        public List<Map<String, Object>> getQuizResults(Long quizId) {
                List<QuizAttempt> attempts = attemptRepo.findByQuizId(quizId);
                return attempts.stream().map(a -> {
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("attemptId", a.getId());
                        row.put("studentName", a.getStudent().getFullName());
                        row.put("rollNumber", a.getStudent().getRollNumber());
                        row.put("score", a.getScore());
                        row.put("totalMarks", a.getQuiz().getTotalMarks());
                        row.put("correctAnswers", a.getCorrectAnswers());
                        row.put("totalQuestions", a.getTotalQuestions());
                        row.put("status", a.getStatus().name());
                        row.put("submittedAt", a.getSubmittedAt());
                        row.put("passed", a.getScore() >= a.getQuiz().getPassingMarks());
                        return row;
                }).collect(Collectors.toList());
        }

        // -------------------------------------------------------
        // STUDENT: List published quizzes for their class
        // -------------------------------------------------------
        public List<QuizDto> getPublishedQuizzesForStudent(Long classId, Long studentId) {

                // Get all quizzes for class
                List<Quiz> quizzes = quizRepo.findBySchoolClassIdOrderByCreatedAtDesc(classId);

                return quizzes.stream()
                                .filter(q -> {

                                        Optional<QuizAttempt> attempt = attemptRepo.findByQuizIdAndStudentId(
                                                        q.getId(),
                                                        studentId);

                                        // Show if published
                                        if (q.getStatus() == Quiz.QuizStatus.PUBLISHED) {
                                                return true;
                                        }

                                        // Show closed quiz only if already attempted
                                        return q.getStatus() == Quiz.QuizStatus.CLOSED
                                                        && attempt.isPresent();
                                })

                                .map(q -> {

                                        QuizDto dto = toDto(q, studentId);

                                        Optional<QuizAttempt> att = attemptRepo.findByQuizIdAndStudentId(
                                                        q.getId(),
                                                        studentId);

                                        if (att.isPresent()) {

                                                QuizAttempt a = att.get();

                                                dto.setAttempted(true);

                                                dto.setSubmitted(
                                                                a.getStatus() != QuizAttempt.AttemptStatus.IN_PROGRESS);

                                                dto.setAttemptStatus(
                                                                a.getStatus().name());

                                                dto.setScore(a.getScore());

                                                dto.setCorrectAnswers(
                                                                a.getCorrectAnswers());

                                        } else {

                                                dto.setAttempted(false);
                                                dto.setSubmitted(false);
                                        }

                                        return dto;

                                }).collect(Collectors.toList());
        }

        // -------------------------------------------------------
        // STUDENT: Start a quiz (creates attempt, returns questions WITHOUT answers)
        // -------------------------------------------------------
        @Transactional
        public Map<String, Object> startQuiz(Long quizId, Long studentId) {
                System.out.println(" **********************START QUIZ CALLED: quizId=" + quizId + ", studentId=" + studentId);
                Quiz quiz = quizRepo.findById(quizId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Quiz not found"));

                LocalDateTime now = LocalDateTime.now();

                Optional<QuizAttempt> existing = attemptRepo.findByQuizIdAndStudentId(
                                quizId,
                                studentId);

                // Quiz not yet published
                if (quiz.getScheduledAt() != null &&
                                now.isBefore(quiz.getScheduledAt())) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Quiz is not available yet");
                }

                // Quiz expired
                if (quiz.getExpiresAt() != null &&
                                now.isAfter(quiz.getExpiresAt())) {

                        quiz.setStatus(Quiz.QuizStatus.CLOSED);
                        quizRepo.save(quiz);

                        // Auto-finish in-progress attempt
                        if (existing.isPresent() &&
                                        existing.get().getStatus() == QuizAttempt.AttemptStatus.IN_PROGRESS) {

                                QuizAttempt a = existing.get();

                                a.setStatus(
                                                QuizAttempt.AttemptStatus.TIMED_OUT);

                                a.setSubmittedAt(now);

                                attemptRepo.save(a);
                        }

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Quiz has expired");
                }

                // Not published
                if (quiz.getStatus() != Quiz.QuizStatus.PUBLISHED) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Quiz is not available");
                }

                // Already submitted
                if (existing.isPresent() &&
                                existing.get().getStatus() != QuizAttempt.AttemptStatus.IN_PROGRESS) {

                        QuizAttempt oldAttempt = existing.get();

                        long elapsedMinutes = java.time.Duration.between(
                                        oldAttempt.getStartedAt(),
                                        now).toMinutes();

                        if (elapsedMinutes >= quiz.getDurationMins()) {

                                oldAttempt.setStatus(
                                                QuizAttempt.AttemptStatus.TIMED_OUT);

                                oldAttempt.setSubmittedAt(now);

                                attemptRepo.save(oldAttempt);

                                existing = Optional.empty();
                        }
                }

                QuizAttempt attempt = existing.orElseGet(() -> {

                        Student student = studentRepo.findById(studentId)
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Student not found"));

                        QuizAttempt a = new QuizAttempt();

                        a.setQuiz(quiz);
                        a.setStudent(student);
                        a.setStartedAt(now);
                        a.setTotalQuestions(
                                        quiz.getQuestions().size());

                        return attemptRepo.save(a);
                });

                List<QuizQuestionDto> questions = quiz.getQuestions()
                                .stream()
                                .map(q -> {

                                        QuizQuestionDto dto = new QuizQuestionDto();

                                        dto.setId(q.getId());
                                        dto.setQuestionText(q.getQuestionText());
                                        dto.setOptionA(q.getOptionA());
                                        dto.setOptionB(q.getOptionB());
                                        dto.setOptionC(q.getOptionC());
                                        dto.setOptionD(q.getOptionD());
                                        dto.setMarks(q.getMarks());
                                        dto.setQuestionOrder(q.getQuestionOrder());

                                        return dto;
                                })
                                .collect(Collectors.toList());

                Map<String, Object> response = new LinkedHashMap<>();

                response.put("attemptId", attempt.getId());
                response.put("quizId", quiz.getId());
                response.put("title", quiz.getTitle());
                response.put("subject", quiz.getSubject().name());
                response.put("durationMins", quiz.getDurationMins());
                response.put("totalMarks", quiz.getTotalMarks());
                response.put("startedAt", attempt.getStartedAt());
                response.put("questions", questions);

                return response;
        }

        @Transactional(readOnly = true)
        public QuizDto getQuiz(Long quizId) {

                Quiz quiz = quizRepo.findById(quizId)
                                .orElseThrow(() -> new RuntimeException("Quiz not found"));

                return toDto(quiz, null);
        }

        @Transactional
        public void deleteQuiz(Long quizId) {

                Quiz quiz = quizRepo.findById(quizId)
                                .orElseThrow(() -> new RuntimeException("Quiz not found"));

                quizRepo.delete(quiz);
        }

        // -------------------------------------------------------
        // STUDENT: Submit quiz answers
        // -------------------------------------------------------
        @Transactional
        public QuizResultDto submitQuiz(QuizSubmitDto dto, Long studentId) {
                QuizAttempt attempt = attemptRepo.findById(dto.getAttemptId())
                                .orElseThrow(() -> new RuntimeException("Attempt not found"));

                if (!attempt.getStudent().getId().equals(studentId)) {
                        throw new RuntimeException("Unauthorized");
                }
                if (attempt.getStatus() != QuizAttempt.AttemptStatus.IN_PROGRESS) {
                        throw new RuntimeException("Quiz already submitted");
                }

                Quiz quiz = attempt.getQuiz();
                List<QuizQuestion> questions = quiz.getQuestions() != null ? quiz.getQuestions() : List.of();

                int score = 0;
                int correct = 0;
                List<QuizAttemptAnswer> savedAnswers = new ArrayList<>();

                for (QuizQuestion q : questions) {
                        String chosen = dto.getAnswers() != null ? dto.getAnswers().get(q.getId()) : null;
                        QuizQuestion.Option chosenOpt = null;
                        boolean isCorrect = false;

                        if (chosen != null) {
                                try {
                                        chosenOpt = QuizQuestion.Option.valueOf(chosen.toUpperCase());
                                        isCorrect = chosenOpt == q.getCorrectOption();
                                } catch (IllegalArgumentException ignored) {
                                }
                        }

                        if (isCorrect) {
                                score += q.getMarks();
                                correct++;
                        }

                        QuizAttemptAnswer ans = new QuizAttemptAnswer();
                        ans.setAttempt(attempt);
                        ans.setQuestion(q);
                        ans.setChosenOption(chosenOpt);
                        ans.setCorrect(isCorrect);
                        savedAnswers.add(answerRepo.save(ans));
                }

                attempt.setScore(score);
                attempt.setCorrectAnswers(correct);
                attempt.setTotalQuestions(questions.size());
                attempt.setStatus(QuizAttempt.AttemptStatus.SUBMITTED);
                attempt.setSubmittedAt(LocalDateTime.now());
                attemptRepo.save(attempt);

                return buildResult(attempt, savedAnswers, quiz);
        }

        public List<Map<String, Object>> getStudentQuizzes(
                        Long classId,
                        Long studentId) {

                List<Quiz> quizzes = quizRepo.findBySchoolClassId(classId);

                return quizzes.stream()
                                .map(q -> {

                                        Map<String, Object> item = new HashMap<>();

                                        item.put("id", q.getId());
                                        item.put("title", q.getTitle());
                                        item.put("subject", q.getSubject());
                                        item.put("status", q.getStatus());

                                        Optional<QuizAttempt> attempt = attemptRepo.findByQuizIdAndStudentId(
                                                        q.getId(),
                                                        studentId);

                                        item.put(
                                                        "attempted",
                                                        attempt.isPresent());

                                        if (attempt.isPresent()) {

                                                item.put(
                                                                "score",
                                                                attempt.get().getScore());

                                                item.put(
                                                                "submitted",
                                                                attempt.get().getStatus() == QuizAttempt.AttemptStatus.SUBMITTED);
                                        }

                                        return item;
                                })
                                .toList();
        }

        // -------------------------------------------------------
        // STUDENT: Get result of a submitted quiz
        // -------------------------------------------------------
        public QuizResultDto getResult(Long quizId, Long studentId) {
                QuizAttempt attempt = attemptRepo.findByQuizIdAndStudentId(quizId, studentId)
                                .orElseThrow(() -> new RuntimeException("No attempt found"));
                if (attempt.getStatus() == QuizAttempt.AttemptStatus.IN_PROGRESS) {
                        throw new RuntimeException("Quiz not yet submitted");
                }
                List<QuizAttemptAnswer> answers = answerRepo.findByAttemptId(attempt.getId());
                return buildResult(attempt, answers, attempt.getQuiz());
        }

        // -------------------------------------------------------
        // Helpers
        // -------------------------------------------------------
        private QuizResultDto buildResult(QuizAttempt attempt, List<QuizAttemptAnswer> answers, Quiz quiz) {
                Map<Long, QuizAttemptAnswer> ansMap = answers.stream()
                                .collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a));

                List<QuizQuestionDto> qdtos = (quiz.getQuestions() != null ? quiz.getQuestions()
                                : Collections.<QuizQuestion>emptyList())
                                .stream().map(q -> {
                                        QuizQuestionDto qdto = new QuizQuestionDto();
                                        qdto.setId(q.getId());
                                        qdto.setQuestionText(q.getQuestionText());
                                        qdto.setOptionA(q.getOptionA());
                                        qdto.setOptionB(q.getOptionB());
                                        qdto.setOptionC(q.getOptionC());
                                        qdto.setOptionD(q.getOptionD());
                                        qdto.setMarks(q.getMarks());
                                        qdto.setQuestionOrder(q.getQuestionOrder());
                                        qdto.setCorrectOption(q.getCorrectOption().name());
                                        QuizAttemptAnswer a = ansMap.get(q.getId());
                                        if (a != null) {
                                                qdto.setChosenOption(
                                                                a.getChosenOption() != null ? a.getChosenOption().name()
                                                                                : null);
                                                qdto.setCorrect(a.isCorrect());
                                        }
                                        return qdto;
                                }).collect(Collectors.toList());

                QuizResultDto result = new QuizResultDto();
                result.setAttemptId(attempt.getId());
                result.setQuizId(quiz.getId());
                result.setQuizTitle(quiz.getTitle());
                result.setSubject(quiz.getSubject().name());
                result.setScore(attempt.getScore());
                result.setTotalMarks(quiz.getTotalMarks());
                result.setCorrectAnswers(attempt.getCorrectAnswers());
                result.setTotalQuestions(attempt.getTotalQuestions());
                result.setPassingMarks(quiz.getPassingMarks());
                result.setPassed(attempt.getScore() >= quiz.getPassingMarks());
                result.setStatus(attempt.getStatus().name());
                result.setSubmittedAt(attempt.getSubmittedAt());
                result.setQuestions(qdtos);
                return result;
        }

        @Transactional
        public QuizDto updateQuiz(Long quizId, QuizDto dto) {

                Quiz quiz = quizRepo.findById(quizId)
                                .orElseThrow(() -> new RuntimeException("Quiz not found"));

                SchoolClass cls = classRepo.findById(dto.getClassId())
                                .orElseThrow(() -> new RuntimeException("Class not found"));

                // Update quiz fields
                quiz.setTitle(dto.getTitle());
                quiz.setDescription(dto.getDescription());
                quiz.setSchoolClass(cls);

                quiz.setSubject(
                                Quiz.Subject.valueOf(
                                                dto.getSubject().toUpperCase()));

                quiz.setDurationMins(dto.getDurationMins());
                quiz.setTotalMarks(dto.getTotalMarks());
                quiz.setPassingMarks(dto.getPassingMarks());
                quiz.setScheduledAt(dto.getScheduledAt());
                quiz.setExpiresAt(dto.getExpiresAt());

                // Remove old questions
                quiz.getQuestions().clear();

                // Add new questions
                if (dto.getQuestions() != null) {

                        int order = 1;

                        for (QuizQuestionDto qdto : dto.getQuestions()) {

                                QuizQuestion question = new QuizQuestion();

                                question.setQuiz(quiz);

                                question.setQuestionText(
                                                qdto.getQuestionText());

                                question.setOptionA(
                                                qdto.getOptionA());

                                question.setOptionB(
                                                qdto.getOptionB());

                                question.setOptionC(
                                                qdto.getOptionC());

                                question.setOptionD(
                                                qdto.getOptionD());

                                if (qdto.getCorrectOption() != null) {
                                        question.setCorrectOption(
                                                        QuizQuestion.Option.valueOf(
                                                                        qdto.getCorrectOption()
                                                                                        .toUpperCase()));
                                }

                                question.setMarks(

                                                qdto.getMarks() > 0
                                                                ? qdto.getMarks()
                                                                : 1);

                                question.setQuestionOrder(order++);

                                quiz.getQuestions().add(question);
                        }
                }

                Quiz savedQuiz = quizRepo.save(quiz);

                return toDto(savedQuiz, null);
        }

        private QuizDto toDto(Quiz q, Long studentId) {

                QuizDto dto = new QuizDto();

                dto.setId(q.getId());
                dto.setTitle(q.getTitle());
                dto.setDescription(q.getDescription());

                if (q.getSchoolClass() != null) {
                        dto.setClassId(
                                        q.getSchoolClass().getId());

                        dto.setClassName(
                                        q.getSchoolClass().getName());
                }

                dto.setSubject(
                                q.getSubject() != null
                                                ? q.getSubject().name()
                                                : null);

                dto.setDurationMins(
                                q.getDurationMins());

                dto.setTotalMarks(
                                q.getTotalMarks());

                dto.setPassingMarks(
                                q.getPassingMarks());

                dto.setScheduledAt(
                                q.getScheduledAt());

                dto.setExpiresAt(
                                q.getExpiresAt());

                dto.setStatus(
                                q.getStatus() != null
                                                ? q.getStatus().name()
                                                : null);

                // Safe questions list
                List<QuizQuestion> rawQuestions = q.getQuestions() != null
                                ? q.getQuestions()
                                : new ArrayList<>();

                dto.setQuestionCount(
                                rawQuestions.size());

                // Convert question entities → DTOs
                List<QuizQuestionDto> questionDtos = rawQuestions.stream()
                                .map(question -> {

                                        QuizQuestionDto qdto = new QuizQuestionDto();

                                        qdto.setId(
                                                        question.getId());

                                        qdto.setQuestionText(
                                                        question.getQuestionText());

                                        qdto.setOptionA(
                                                        question.getOptionA());

                                        qdto.setOptionB(
                                                        question.getOptionB());

                                        qdto.setOptionC(
                                                        question.getOptionC());

                                        qdto.setOptionD(
                                                        question.getOptionD());

                                        // FIXED NULL ISSUE
                                        qdto.setCorrectOption(
                                                        question.getCorrectOption() != null
                                                                        ? question.getCorrectOption().name()
                                                                        : null);

                                        qdto.setMarks(
                                                        question.getMarks());

                                        qdto.setQuestionOrder(
                                                        question.getQuestionOrder());

                                        return qdto;

                                })
                                .collect(Collectors.toList());

                dto.setQuestions(questionDtos);

                return dto;
        }
}