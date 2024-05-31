package com.example.financeback.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RegisterTest {

    @Test
    fun `usuario nao vazio`() {
        val result = Utils().validateRegister(
            "",
            "joao",
            "12345",
            "12345"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `nome completo nao vazio`() {
        val result = Utils().validateRegister(
            "joao",
            "",
            "12345",
            "12345"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `senha valida`() {
        val result = Utils().validateRegister(
            "joao",
            "joao victor",
            "123",
            "12345"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `senha de confirmacao igual a senha`() {
        val result = Utils().validateRegister(
            "joao",
            "joao victor",
            "12345",
            "54321"
        )
        assertThat(result).isFalse()
    }
}