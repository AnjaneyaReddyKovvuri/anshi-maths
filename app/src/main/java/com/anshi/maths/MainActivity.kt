package com.anshi.maths

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anshi.maths.ui.AnshiMathsTheme
import com.anshi.maths.ui.HomeScreen
import com.anshi.maths.ui.QuizScreen
import com.anshi.maths.ui.ResultScreen

class MainActivity : ComponentActivity() {

    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnshiMathsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    BackHandler(enabled = state.screen != Screen.HOME) { viewModel.goHome() }
                    when (state.screen) {
                        Screen.HOME -> HomeScreen(
                            settings = state.settings,
                            onSettingsChange = viewModel::updateSettings,
                            onStart = viewModel::startQuiz,
                        )
                        Screen.QUIZ -> QuizScreen(
                            quiz = state.quiz,
                            secondsPerQuestion = state.settings.secondsPerQuestion,
                            onAnswer = viewModel::answer,
                            onQuit = viewModel::goHome,
                        )
                        Screen.RESULT -> ResultScreen(
                            quiz = state.quiz,
                            onPlayAgain = viewModel::startQuiz,
                            onHome = viewModel::goHome,
                        )
                    }
                }
            }
        }
    }
}
