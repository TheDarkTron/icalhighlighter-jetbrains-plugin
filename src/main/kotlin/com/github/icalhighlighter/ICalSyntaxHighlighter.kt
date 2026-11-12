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
            DefaultLanguageHighlighterColors.INSTANCE_FIELD
        )
        val PROPERTY_VALUE = TextAttributesKey.createTextAttributesKey(
            "ICAL_PROPERTY_VALUE",
            DefaultLanguageHighlighterColors.STRING
        )
        val PARAMETER_NAME = TextAttributesKey.createTextAttributesKey(
            "ICAL_PARAMETER_NAME",
            DefaultLanguageHighlighterColors.METADATA
        )
        val PARAMETER_VALUE = TextAttributesKey.createTextAttributesKey(
            "ICAL_PARAMETER_VALUE",
            DefaultLanguageHighlighterColors.NUMBER
        )
        val BLOCK_KEYWORD = TextAttributesKey.createTextAttributesKey(
            "ICAL_BLOCK_KEYWORD",
            DefaultLanguageHighlighterColors.KEYWORD
        )
        val COMPONENT = TextAttributesKey.createTextAttributesKey(
            "ICAL_COMPONENT",
            DefaultLanguageHighlighterColors.CLASS_NAME
        )
        val SEPARATOR = TextAttributesKey.createTextAttributesKey(
            "ICAL_SEPARATOR",
            DefaultLanguageHighlighterColors.OPERATION_SIGN
        )
        val VALUE_SEPARATOR = TextAttributesKey.createTextAttributesKey(
            "ICAL_VALUE_SEPARATOR",
            DefaultLanguageHighlighterColors.SEMICOLON
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
            ICalTokenTypes.PARAMETER_NAME -> arrayOf(PARAMETER_NAME)
            ICalTokenTypes.PARAMETER_VALUE -> arrayOf(PARAMETER_VALUE)
            ICalTokenTypes.BLOCK_KEYWORD -> arrayOf(BLOCK_KEYWORD)
            ICalTokenTypes.COMPONENT -> arrayOf(COMPONENT)
            ICalTokenTypes.SEPARATOR -> arrayOf(SEPARATOR)
            ICalTokenTypes.VALUE_SEPARATOR -> arrayOf(VALUE_SEPARATOR)
            TokenType.BAD_CHARACTER -> arrayOf(BAD_CHARACTER)
            else -> emptyArray()
        }
    }
}
