package com.anshi.maths.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshi.maths.QuizState
import java.util.Locale

@Composable
fun ResultScreen(
    quiz: QuizState,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
) {
    val total = quiz.records.size.coerceAtLeast(1)
    val percent = quiz.score * 100 / total
    val stars = when {
        percent >= 90 -> 3
        percent >= 70 -> 2
        percent >= 40 -> 1
        else -> 0
    }
    val headline = when (stars) {
        3 -> "Superstar, Anshi! 🏆"
        2 -> "Well done, Anshi! 🎉"
        1 -> "Good try, Anshi! 👍"
        else -> "Keep practising, Anshi! 💪"
    }
    val answered = quiz.records.filter { it.chosen != null }
    val avgSeconds = if (answered.isEmpty()) 0.0 else answered.map { it.timeTakenMs }.average() / 1000
    val mistakes = quiz.records.filterNot { it.isCorrect }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(headline, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Text("⭐".repeat(stars) + "☆".repeat(3 - stars), fontSize = 44.sp, color = Sun)
        Spacer(Modifier.height(8.dp))
        Text("${quiz.score} / ${quiz.records.size}", fontSize = 56.sp, fontWeight = FontWeight.Bold)
        Text(
            "$percent% correct  •  ${String.format(Locale.US, "%.1f", avgSeconds)}s per answer",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )

        Spacer(Modifier.height(16.dp))
        if (mistakes.isNotEmpty()) {
            Text("Let's learn these:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(mistakes) { record ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "${record.question.text} = ${record.question.answer}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = CorrectGreen,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                record.chosen?.let { "you said $it" } ?: "time up",
                                fontSize = 14.sp,
                                color = WrongRed,
                            )
                        }
                    }
                }
            }
        } else {
            Spacer(Modifier.weight(1f))
            Text("No mistakes at all! 🌟", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CorrectGreen)
            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onPlayAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ),
        ) { Text("Play again", fontSize = 24.sp, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
        ) { Text("Change settings", fontSize = 18.sp) }
    }
}
