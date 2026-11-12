package com.github.icalhighlighter

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType
import com.intellij.psi.TokenType

class ICalLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var bufferEnd: Int = 0
    private var bufferStart: Int = 0
    private var currentPosition: Int = 0
    private var tokenStart: Int = 0
    private var tokenType: IElementType? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        this.bufferStart = startOffset
        this.currentPosition = startOffset
        this.tokenStart = startOffset
        advance()
    }

    override fun getState(): Int = 0

    override fun getTokenType(): IElementType? {
        return tokenType
    }

    override fun getTokenStart(): Int = tokenStart

    override fun getTokenEnd(): Int = currentPosition

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = bufferEnd

    override fun advance() {
        tokenStart = currentPosition

        if (currentPosition >= bufferEnd) {
            tokenType = null
            return
        }

        // Skip whitespace
        if (isWhitespace(buffer[currentPosition])) {
            skipWhitespace()
            tokenType = TokenType.WHITE_SPACE
            return
        }

        when {
            isBeginComponent() -> {
                tokenType = ICalTokenTypes.COMPONENT
                currentPosition += "BEGIN:".length
                while (currentPosition < bufferEnd && !isNewLine(buffer[currentPosition])) {
                    currentPosition++
                }
                if (currentPosition < bufferEnd && isNewLine(buffer[currentPosition])) {
                    currentPosition++
                }
            }
            isEndComponent() -> {
                tokenType = ICalTokenTypes.COMPONENT
                currentPosition += "END:".length
                while (currentPosition < bufferEnd && !isNewLine(buffer[currentPosition])) {
                    currentPosition++
                }
                if (currentPosition < bufferEnd && isNewLine(buffer[currentPosition])) {
                    currentPosition++
                }
            }
            isPropertyKey() -> {
                tokenType = ICalTokenTypes.PROPERTY_KEY
                while (currentPosition < bufferEnd && !isSeparator(buffer[currentPosition]) && !isNewLine(buffer[currentPosition])) {
                    currentPosition++
                }
            }
            isSeparator(buffer[currentPosition]) -> {
                tokenType = ICalTokenTypes.SEPARATOR
                currentPosition++
            }
            else -> {
                tokenType = ICalTokenTypes.PROPERTY_VALUE
                while (currentPosition < bufferEnd && !isNewLine(buffer[currentPosition])) {
                    currentPosition++
                }
                if (currentPosition < bufferEnd && isNewLine(buffer[currentPosition])) {
                    currentPosition++
                }
            }
        }

        // Ensure we always advance at least one character if we haven't reached the end
        if (tokenStart == currentPosition && currentPosition < bufferEnd) {
            currentPosition++
            tokenType = TokenType.BAD_CHARACTER
        }
    }

    private fun skipWhitespace() {
        while (currentPosition < bufferEnd && isWhitespace(buffer[currentPosition])) {
            currentPosition++
        }
    }

    private fun isBeginComponent(): Boolean {
        return currentPosition + 5 < bufferEnd &&
                buffer.subSequence(currentPosition, currentPosition + 6).toString().uppercase() == "BEGIN:"
    }

    private fun isEndComponent(): Boolean {
        return currentPosition + 3 < bufferEnd &&
                buffer.subSequence(currentPosition, currentPosition + 4).toString().uppercase() == "END:"
    }

    private fun isPropertyKey(): Boolean {
        var pos = currentPosition
        while (pos < bufferEnd && !isSeparator(buffer[pos]) && !isNewLine(buffer[pos])) {
            pos++
        }
        return pos < bufferEnd && isSeparator(buffer[pos])
    }

    private fun isNewLine(c: Char): Boolean = c == '\n' || c == '\r'

    private fun isWhitespace(c: Char): Boolean = c.isWhitespace()

    private fun isSeparator(c: Char): Boolean = c == ':'
} 