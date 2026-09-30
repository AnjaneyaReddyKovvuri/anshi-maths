package com.anshi.maths

import kotlin.random.Random

enum class Operation(val symbol: String, val label: String) {
    ADD("+", "Add"),
    SUBTRACT("−", "Subtract"),
    MULTIPLY("×", "Multiply"),
    DIVIDE("÷", "Divide"),
}

data class Question(
    val a: Int,
    val b: Int,
    val operation: Operation,
    val answer: Int,
    val options: List<Int>,
) {
    val text: String get() = "$a ${operation.symbol} $b"
}

data class QuizSettings(
    val operations: Set<Operation> = Operation.entries.toSet(),
    /** Largest times table used for × and ÷ (10 = 10x10, 20 = 20x20). */
    val tableMax: Int = 10,
    /** Largest number used for + and − (sums never exceed this). */
    val addSubMax: Int = 100,
    val secondsPerQuestion: Int = 20,
    val questionCount: Int = 20,
) {
    companion object {
        val TABLE_OPTIONS = listOf(10, 20)
        val ADD_SUB_OPTIONS = listOf(20, 100, 1000)
        val SECONDS_OPTIONS = listOf(5, 10, 20, 30, 60)
        val COUNT_OPTIONS = listOf(10, 20, 30)
    }
}

class QuestionGenerator(private val random: Random = Random.Default) {

    /** Builds a quiz, avoiding repeated questions where the number range allows it. */
    fun generateQuiz(settings: QuizSettings): List<Question> {
        val seen = HashSet<String>()
        return List(settings.questionCount) {
            var question = generate(settings)
            var attempts = 0
            while (question.text in seen && attempts < 30) {
                question = generate(settings)
                attempts++
            }
            seen += question.text
            question
        }
    }

    fun generate(settings: QuizSettings): Question {
        val op = settings.operations.ifEmpty { Operation.entries.toSet() }.random(random)
        val max = settings.addSubMax
        val table = settings.tableMax
        val (a, b, answer) = when (op) {
            Operation.ADD -> {
                val a = random.nextInt(1, max)
                val b = random.nextInt(1, max - a + 1)
                Triple(a, b, a + b)
            }
            Operation.SUBTRACT -> {
                val a = random.nextInt(2, max + 1)
                val b = random.nextInt(1, a + 1)
                Triple(a, b, a - b)
            }
            Operation.MULTIPLY -> {
                val a = random.nextInt(1, table + 1)
                val b = random.nextInt(1, table + 1)
                Triple(a, b, a * b)
            }
            Operation.DIVIDE -> {
                val divisor = random.nextInt(1, table + 1)
                val quotient = random.nextInt(1, table + 1)
                Triple(divisor * quotient, divisor, quotient)
            }
        }
        return Question(a, b, op, answer, buildOptions(a, b, op, answer))
    }

    /** Three plausible wrong answers (the kind of slip a child would make) plus the right one. */
    private fun buildOptions(a: Int, b: Int, op: Operation, answer: Int): List<Int> {
        val nearMisses = when (op) {
            Operation.MULTIPLY -> listOf(
                a * (b + 1), a * (b - 1), (a + 1) * b, (a - 1) * b,
                answer + 1, answer - 1, answer + 10, answer - 10,
            )
            Operation.DIVIDE -> listOf(answer + 1, answer - 1, answer + 2, answer - 2, answer + 3)
            Operation.ADD, Operation.SUBTRACT -> listOf(
                answer + 1, answer - 1, answer + 2, answer - 2,
                answer + 10, answer - 10, answer + 9, answer - 11,
            )
        }
        val minValue = if (op == Operation.DIVIDE) 1 else 0
        val wrong = nearMisses
            .filter { it >= minValue && it != answer }
            .distinct()
            .shuffled(random)
            .take(3)
            .toMutableList()
        var offset = 1
        while (wrong.size < 3) {
            val candidate = answer + offset++
            if (candidate !in wrong) wrong += candidate
        }
        return (wrong + answer).shuffled(random)
    }
}
