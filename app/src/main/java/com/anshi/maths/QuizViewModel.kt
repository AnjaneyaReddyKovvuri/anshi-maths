package com.anshi.maths

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Screen { HOME, QUIZ, RESULT }

data class AnswerRecord(val question: Question, val chosen: Int?, val timeTakenMs: Long) {
    val isCorrect: Boolean get() = chosen == question.answer
}

data class QuizState(
    val questions: List<Question> = emptyList(),
    val index: Int = 0,
    val remainingMs: Long = 0,
    /** Set once the current question is answered or timed out; drives the feedback colours. */
    val revealed: Boolean = false,
    val chosen: Int? = null,
    val records: List<AnswerRecord> = emptyList(),
) {
    val current: Question? get() = questions.getOrNull(index)
    val score: Int get() = records.count { it.isCorrect }
}

data class UiState(
    val screen: Screen = Screen.HOME,
    val settings: QuizSettings = QuizSettings(),
    val quiz: QuizState = QuizState(),
)

class QuizViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val generator = QuestionGenerator()
    private var timerJob: Job? = null

    private val _state = MutableStateFlow(UiState(settings = loadSettings()))
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun updateSettings(settings: QuizSettings) {
        _state.update { it.copy(settings = settings) }
        saveSettings(settings)
    }

    fun startQuiz() {
        val settings = _state.value.settings
        val quiz = QuizState(
            questions = generator.generateQuiz(settings),
            remainingMs = settings.secondsPerQuestion * 1000L,
        )
        _state.update { it.copy(screen = Screen.QUIZ, quiz = quiz) }
        startTimer()
    }

    fun answer(option: Int) {
        if (_state.value.quiz.revealed) return
        reveal(option)
    }

    fun goHome() {
        timerJob?.cancel()
        _state.update { it.copy(screen = Screen.HOME) }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.quiz.remainingMs > 0) {
                delay(TICK_MS)
                _state.update { s ->
                    s.copy(quiz = s.quiz.copy(remainingMs = (s.quiz.remainingMs - TICK_MS).coerceAtLeast(0)))
                }
            }
            reveal(chosen = null)
        }
    }

    private fun reveal(chosen: Int?) {
        timerJob?.cancel()
        val s = _state.value
        val quiz = s.quiz
        val question = quiz.current ?: return
        val taken = s.settings.secondsPerQuestion * 1000L - quiz.remainingMs
        val record = AnswerRecord(question, chosen, taken)
        _state.update {
            it.copy(quiz = quiz.copy(revealed = true, chosen = chosen, records = quiz.records + record))
        }
        timerJob = viewModelScope.launch {
            delay(if (record.isCorrect) CORRECT_PAUSE_MS else WRONG_PAUSE_MS)
            next()
        }
    }

    private fun next() {
        val s = _state.value
        val nextIndex = s.quiz.index + 1
        if (nextIndex >= s.quiz.questions.size) {
            _state.update { it.copy(screen = Screen.RESULT) }
            return
        }
        _state.update {
            it.copy(
                quiz = it.quiz.copy(
                    index = nextIndex,
                    remainingMs = it.settings.secondsPerQuestion * 1000L,
                    revealed = false,
                    chosen = null,
                )
            )
        }
        startTimer()
    }

    private fun loadSettings(): QuizSettings {
        val defaults = QuizSettings()
        val ops = prefs.getStringSet("operations", null)
            ?.mapNotNull { name -> Operation.entries.find { it.name == name } }
            ?.toSet()
            ?.takeIf { it.isNotEmpty() }
            ?: defaults.operations
        return QuizSettings(
            operations = ops,
            tableMax = prefs.getInt("tableMax", defaults.tableMax),
            addSubMax = prefs.getInt("addSubMax", defaults.addSubMax),
            secondsPerQuestion = prefs.getInt("seconds", defaults.secondsPerQuestion),
            questionCount = prefs.getInt("count", defaults.questionCount),
        )
    }

    private fun saveSettings(settings: QuizSettings) {
        prefs.edit()
            .putStringSet("operations", settings.operations.map { it.name }.toSet())
            .putInt("tableMax", settings.tableMax)
            .putInt("addSubMax", settings.addSubMax)
            .putInt("seconds", settings.secondsPerQuestion)
            .putInt("count", settings.questionCount)
            .apply()
    }

    private companion object {
        const val TICK_MS = 100L
        const val CORRECT_PAUSE_MS = 700L
        const val WRONG_PAUSE_MS = 1800L
    }
}
