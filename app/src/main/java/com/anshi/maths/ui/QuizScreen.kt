package com.anshi.maths.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshi.maths.QuizState
import kotlin.math.ceil

@Composable
fun QuizScreen(
    quiz: QuizState,
    secondsPerQuestion: Int,
    onAnswer: (Int) -> Unit,
    onQuit: () -> Unit,
) {
    val question = quiz.current ?: return
    val fraction = quiz.remainingMs / (secondsPerQuestion * 1000f)
    val barColor by animateColorAsState(
        when {
            fraction > 0.5f -> CorrectGreen
            fraction > 0.25f -> Sun
            else -> WrongRed
        },
        label = "timerColor",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onQuit) { Text("✕ Quit", fontSize = 16.sp) }
            Spacer(Modifier.weight(1f))
            Text(
                "Question ${quiz.index + 1} of ${quiz.questions.size}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.weight(1f))
            Text("⭐ ${quiz.score}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier
                    .weight(1f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp)),
                color = barColor,
                trackColor = MaterialTheme.colorScheme.primaryContainer,
                drawStopIndicator = {},
                gapSize = 0.dp,
            )
            Text(
                "${ceil(quiz.remainingMs / 1000.0).toInt()}s",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp),
            )
        }

        Spacer(Modifier.height(28.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "${question.text} = ?",
                    fontSize = if (question.text.length > 9) 44.sp else 54.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 1,
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Box(Modifier.height(40.dp), contentAlignment = Alignment.Center) {
            if (quiz.revealed) {
                val (message, color) = when {
                    quiz.chosen == question.answer -> "Great job! 🎉" to CorrectGreen
                    quiz.chosen == null -> "Time's up! It's ${question.answer}" to WrongRed
                    else -> "Oops! It's ${question.answer}" to WrongRed
                }
                Text(message, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            }
        }
        Spacer(Modifier.height(12.dp))

        question.options.chunked(2).forEach { row ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                row.forEach { option ->
                    AnswerButton(
                        value = option,
                        isAnswer = option == question.answer,
                        isChosen = option == quiz.chosen,
                        revealed = quiz.revealed,
                        modifier = Modifier.weight(1f),
                        onClick = { onAnswer(option) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AnswerButton(
    value: Int,
    isAnswer: Boolean,
    isChosen: Boolean,
    revealed: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val container = when {
        !revealed -> MaterialTheme.colorScheme.surface
        isAnswer -> CorrectGreen
        isChosen -> WrongRed
        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
    }
    val content = when {
        revealed && (isAnswer || isChosen) -> Color.White
        revealed -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.primary
    }
    Button(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = container,
            disabledContentColor = content,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        enabled = !revealed,
    ) {
        Text("$value", fontSize = 38.sp, fontWeight = FontWeight.Bold)
    }
}
