package com.anshi.maths.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anshi.maths.Operation
import com.anshi.maths.QuizSettings

@Composable
fun HomeScreen(
    settings: QuizSettings,
    onSettingsChange: (QuizSettings) -> Unit,
    onStart: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Anshi Maths",
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            "Fast calculation practice",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )
        Spacer(Modifier.height(24.dp))

        Section("What shall we practise?") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Operation.entries.forEach { op ->
                    val selected = op in settings.operations
                    ChoiceButton(
                        label = op.symbol,
                        caption = op.label,
                        selected = selected,
                        modifier = Modifier.weight(1f),
                    ) {
                        val ops = if (selected) settings.operations - op else settings.operations + op
                        // Keep at least one operation selected.
                        if (ops.isNotEmpty()) onSettingsChange(settings.copy(operations = ops))
                    }
                }
            }
        }

        Section("Times tables (× and ÷)") {
            ChoiceRow(QuizSettings.TABLE_OPTIONS, settings.tableMax, { "$it × $it" }) {
                onSettingsChange(settings.copy(tableMax = it))
            }
        }

        Section("Add & subtract up to") {
            ChoiceRow(QuizSettings.ADD_SUB_OPTIONS, settings.addSubMax, { "$it" }) {
                onSettingsChange(settings.copy(addSubMax = it))
            }
        }

        Section("Seconds for each question") {
            ChoiceRow(QuizSettings.SECONDS_OPTIONS, settings.secondsPerQuestion, { "$it" }) {
                onSettingsChange(settings.copy(secondsPerQuestion = it))
            }
        }

        Section("Number of questions") {
            ChoiceRow(QuizSettings.COUNT_OPTIONS, settings.questionCount, { "$it" }) {
                onSettingsChange(settings.copy(questionCount = it))
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ),
        ) {
            Text("Start  ▶", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(
            title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        content()
    }
}

@Composable
private fun <T> ChoiceRow(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            ChoiceButton(
                label = label(option),
                selected = option == selected,
                modifier = Modifier.weight(1f),
            ) { onSelect(option) }
        }
    }
}

@Composable
private fun ChoiceButton(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    caption: String? = null,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    val content: @Composable () -> Unit = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1, textAlign = TextAlign.Center)
            if (caption != null) Text(caption, fontSize = 11.sp, maxLines = 1)
        }
    }
    val height = if (caption != null) 68.dp else 52.dp
    val mod = modifier.height(height)
    val padding = PaddingValues(horizontal = 4.dp)
    if (selected) {
        Button(onClick = onClick, modifier = mod, shape = shape, contentPadding = padding) { content() }
    } else {
        OutlinedButton(onClick = onClick, modifier = mod, shape = shape, contentPadding = padding) { content() }
    }
}
