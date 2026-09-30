package com.anshi.maths

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class QuestionGeneratorTest {

    private val generator = QuestionGenerator(Random(42))

    private fun sample(settings: QuizSettings, n: Int = 5000) = List(n) { generator.generate(settings) }

    @Test
    fun `every question has four distinct non-negative options including the answer`() {
        for (tableMax in QuizSettings.TABLE_OPTIONS) {
            for (addSubMax in QuizSettings.ADD_SUB_OPTIONS) {
                sample(QuizSettings(tableMax = tableMax, addSubMax = addSubMax)).forEach { q ->
                    assertEquals(q.toString(), 4, q.options.size)
                    assertEquals(q.toString(), 4, q.options.toSet().size)
                    assertTrue(q.toString(), q.answer in q.options)
                    assertTrue(q.toString(), q.options.all { it >= 0 })
                }
            }
        }
    }

    @Test
    fun `answers are arithmetically correct`() {
        sample(QuizSettings()).forEach { q ->
            val expected = when (q.operation) {
                Operation.ADD -> q.a + q.b
                Operation.SUBTRACT -> q.a - q.b
                Operation.MULTIPLY -> q.a * q.b
                Operation.DIVIDE -> q.a / q.b
            }
            assertEquals(q.toString(), expected, q.answer)
            if (q.operation == Operation.DIVIDE) assertEquals(q.toString(), 0, q.a % q.b)
        }
    }

    @Test
    fun `numbers stay within the chosen ranges`() {
        for (tableMax in QuizSettings.TABLE_OPTIONS) {
            for (addSubMax in QuizSettings.ADD_SUB_OPTIONS) {
                sample(QuizSettings(tableMax = tableMax, addSubMax = addSubMax)).forEach { q ->
                    when (q.operation) {
                        Operation.ADD -> assertTrue(q.toString(), q.answer <= addSubMax)
                        Operation.SUBTRACT -> assertTrue(q.toString(), q.a <= addSubMax && q.answer >= 0)
                        Operation.MULTIPLY -> assertTrue(q.toString(), q.a in 1..tableMax && q.b in 1..tableMax)
                        Operation.DIVIDE -> assertTrue(q.toString(), q.b in 1..tableMax && q.answer in 1..tableMax)
                    }
                }
            }
        }
    }

    @Test
    fun `only selected operations are used`() {
        val settings = QuizSettings(operations = setOf(Operation.MULTIPLY, Operation.DIVIDE))
        assertTrue(sample(settings).all { it.operation in settings.operations })
    }

    @Test
    fun `a quiz has the requested length without repeats`() {
        val quiz = generator.generateQuiz(QuizSettings(questionCount = 30))
        assertEquals(30, quiz.size)
        assertEquals(30, quiz.map { it.text }.toSet().size)
    }
}
