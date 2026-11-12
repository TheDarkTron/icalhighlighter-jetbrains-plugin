package com.github.icalhighlighter

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

/** Content-line lexer shared by iCalendar and vCard. State belongs to the start of each token. */
class ICalLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var end = 0
    private var position = 0
    private var tokenStart = 0
    private var tokenState = PROPERTY_NAME
    private var nextState = PROPERTY_NAME
    private var tokenType: IElementType? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        end = endOffset
        position = startOffset
        nextState = initialState
        advance()
    }

    override fun getState(): Int = tokenState
    override fun getTokenType(): IElementType? = tokenType
    override fun getTokenStart(): Int = tokenStart
    override fun getTokenEnd(): Int = position
    override fun getBufferSequence(): CharSequence = buffer
    override fun getBufferEnd(): Int = end

    override fun advance() {
        tokenStart = position
        tokenState = nextState
        if (position >= end) {
            tokenType = null
            return
        }

        if (isNewline(buffer[position])) {
            if (buffer[position++] == '\r' && position < end && buffer[position] == '\n') position++
            if (position < end && buffer[position] in " \t") {
                // A folded physical line continues the same logical property, including quoted parameters.
                position++
            } else {
                nextState = PROPERTY_NAME
            }
            tokenType = TokenType.WHITE_SPACE
            return
        }

        when (nextState) {
            PROPERTY_VALUE, ESCAPED_PROPERTY_VALUE -> lexPropertyValue()
            COMPONENT_VALUE -> {
                while (position < end && !isNewline(buffer[position])) position++
                tokenType = if (nextState == COMPONENT_VALUE) ICalTokenTypes.COMPONENT else ICalTokenTypes.PROPERTY_VALUE
            }
            PARAMETER_VALUE, QUOTED_PARAMETER_VALUE -> lexParameterValue()
            else -> lexHeader()
        }
    }

    private fun lexPropertyValue() {
        var escaped = nextState == ESCAPED_PROPERTY_VALUE
        if (!escaped && buffer[position] == ';') {
            position++
            tokenType = ICalTokenTypes.VALUE_SEPARATOR
            return
        }
        while (position < end && !isNewline(buffer[position])) {
            val character = buffer[position]
            if (!escaped && character == ';') break
            escaped = !escaped && character == '\\'
            position++
        }
        nextState = if (escaped) ESCAPED_PROPERTY_VALUE else PROPERTY_VALUE
        tokenType = ICalTokenTypes.PROPERTY_VALUE
    }

    private fun lexHeader() {
        val character = buffer[position]
        if (character in " \t") {
            while (position < end && buffer[position] in " \t") position++
            tokenType = TokenType.WHITE_SPACE
            return
        }
        if (character in ";:=,") {
            position++
            tokenType = ICalTokenTypes.SEPARATOR
            nextState = when (character) {
                ':' -> if (nextState == COMPONENT_HEADER) COMPONENT_VALUE else PROPERTY_VALUE
                ';' -> PARAMETER_NAME
                '=' -> PARAMETER_VALUE
                else -> nextState
            }
            return
        }
        while (position < end && buffer[position] !in ";:=, \t\r\n") position++
        val name = buffer.subSequence(tokenStart, position).toString()
        val isComponent = nextState == PROPERTY_NAME && position < end && buffer[position] == ':' &&
            (name.equals("BEGIN", ignoreCase = true) || name.equals("END", ignoreCase = true))
        tokenType = when {
            isComponent -> ICalTokenTypes.BLOCK_KEYWORD
            nextState == PARAMETER_NAME -> ICalTokenTypes.PARAMETER_NAME
            nextState == PROPERTY_NAME -> ICalTokenTypes.PROPERTY_KEY
            else -> TokenType.BAD_CHARACTER
        }
        nextState = if (isComponent) COMPONENT_HEADER else if (nextState == PARAMETER_NAME) PARAMETER_NAME else PROPERTY_NAME
    }

    private fun lexParameterValue() {
        var quoted = nextState == QUOTED_PARAMETER_VALUE
        if (!quoted && buffer[position] in ";:,") {
            val delimiter = buffer[position++]
            tokenType = ICalTokenTypes.SEPARATOR
            nextState = when (delimiter) {
                ';' -> PARAMETER_NAME
                ':' -> PROPERTY_VALUE
                else -> PARAMETER_VALUE
            }
            return
        }
        while (position < end && !isNewline(buffer[position])) {
            val character = buffer[position]
            if (!quoted && character in ";:,") break
            if (character == '"') quoted = !quoted
            position++
        }
        nextState = if (quoted) QUOTED_PARAMETER_VALUE else PARAMETER_VALUE
        tokenType = ICalTokenTypes.PARAMETER_VALUE
    }

    private fun isNewline(character: Char): Boolean = character == '\r' || character == '\n'

    private companion object {
        const val PROPERTY_NAME = 0
        const val COMPONENT_HEADER = 2
        const val PARAMETER_NAME = 3
        const val PARAMETER_VALUE = 4
        const val QUOTED_PARAMETER_VALUE = 5
        const val PROPERTY_VALUE = 6
        const val COMPONENT_VALUE = 7
        const val ESCAPED_PROPERTY_VALUE = 8
    }
}
