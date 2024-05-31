package com.example.financeback.utils

import com.example.financeback.classes.IncomeInfo
import com.google.common.truth.Truth.assertThat


import org.junit.Test

class IncomeTest {

    @Test
    fun `nota fiscal com valor nao zerado`() {
        val result = Utils().validateIncome(
            IncomeInfo(
                1,
                0.0,
                "cafe",
                System.currentTimeMillis(),
                "",
                1
            )
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `nota fiscal com nome valido`() {
        val result = Utils().validateIncome(
            IncomeInfo(
                1,
                5.99,
                "",
                System.currentTimeMillis(),
                "",
                1
            )
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `nota fiscal com id de categoria valido`() {
        val result = Utils().validateIncome(
            IncomeInfo(
                1,
                5.99,
                "cafe",
                System.currentTimeMillis(),
                "",
                -1
            )
        )
        assertThat(result).isFalse()
    }
}