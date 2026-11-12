package com.github.icalhighlighter

import com.intellij.lang.Language

class ICalLanguage private constructor() : Language("iCal") {
    companion object {
        val INSTANCE = ICalLanguage()
    }
} 