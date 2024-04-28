package com.example.financeback.utils

class NumberFormatter {
    fun currencyFormatter(value: String,
                          numberOfDecimals: Int = 2): String{
        val intPart = value
            .dropLast(numberOfDecimals)
            .reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()
            .ifEmpty {
                "0"
            }

        val fractionPart = value.takeLast(numberOfDecimals).let {
            if (it.length != numberOfDecimals) {
                List(numberOfDecimals - it.length) {
                    0
                }.joinToString("") + it
            } else {
                it
            }
        }

        return "${intPart},${fractionPart}"
    }

    fun currencyFormatterFloat(value: String,
                               numberOfDecimals: Int = 2): String{
        val numberWithoutComma = value.replace(".", "")

        val intPart = numberWithoutComma
            .dropLast(numberOfDecimals)
            .reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()
            .ifEmpty {
                "0"
            }

        val fractionPart = numberWithoutComma.takeLast(numberOfDecimals).let {
            if (it.length != numberOfDecimals) {
                List(numberOfDecimals - it.length) {
                    0
                }.joinToString("") + it
            } else {
                it
            }
        }

        return "${intPart},${fractionPart}"
    }

    fun decimalFormatter(value: String): String{
        return value.toString().takeLast(2).let{
            if(it.length != 2)
                List(2 - it.length){
                    0
                }.joinToString("") + it
            else it
        }
    }
}