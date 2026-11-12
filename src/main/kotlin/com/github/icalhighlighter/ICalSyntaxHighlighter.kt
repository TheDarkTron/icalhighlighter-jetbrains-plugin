package com.github.icalhighlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class ICalSyntaxHighlighter : SyntaxHighlighterBase() {
    companion object {
        val PROPERTY_KEY = TextAttributesKey.createTextAttributesKey(
            "ICAL_PROPERTY_KEY",
            DefaultLanguageHighlighterColors.KEYWORD
        )
        val PROPERTY_VALUE = TextAttributesKey.createTextAttributesKey(
            "ICAL_PROPERTY_VALUE",
            DefaultLanguageHighlighterColors.STRING
        )
        val COMPONENT = TextAttributesKey.createTextAttributesKey(
            "ICAL_COMPONENT",
            DefaultLanguageHighlighterColors.CLASS_NAME
        )
        val SEPARATOR = TextAttributesKey.createTextAttributesKey(
            "ICAL_SEPARATOR",
            DefaultLanguageHighlighterColors.OPERATION_SIGN
        )
        val BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(
            "ICAL_BAD_CHARACTER",
            HighlighterColors.BAD_CHARACTER
        )
    }

    override fun getHighlightingLexer(): Lexer {
        return ICalLexer()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            ICalTokenTypes.PROPERTY_KEY -> arrayOf(PROPERTY_KEY)
            ICalTokenTypes.PROPERTY_VALUE -> arrayOf(PROPERTY_VALUE)
            ICalTokenTypes.COMPONENT -> arrayOf(COMPONENT)
            ICalTokenTypes.SEPARATOR -> arrayOf(SEPARATOR)
            TokenType.BAD_CHARACTER -> arrayOf(BAD_CHARACTER)
            else -> emptyArray()
        }
    }
} 