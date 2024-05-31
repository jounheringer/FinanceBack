package com.example.financeback.utils


import com.google.common.truth.Truth.assertThat
import org.junit.Test
class LoginTest {

    @Test
    fun `usuario valido`() {
        val result = Utils().validateLogin(
            "",
            "12345"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `senha valida`() {
        val result = Utils().validateLogin(
            "joao",
            "123"
        )
        assertThat(result).isFalse()
    }
}

