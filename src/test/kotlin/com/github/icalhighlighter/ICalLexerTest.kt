package com.github.icalhighlighter

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ICalLexerTest {
    private data class Token(val type: IElementType, val text: String, val offset: Int, val state: Int)

    private fun tokens(text: String, offset: Int = 0, state: Int = 0): List<Token> {
        val lexer = ICalLexer()
        lexer.start(text, offset, text.length, state)
        val result = mutableListOf<Token>()
        while (lexer.tokenType != null) {
            assertTrue("Lexer must advance at ${lexer.tokenStart}", lexer.tokenEnd > lexer.tokenStart)
            result += Token(lexer.tokenType!!, text.substring(lexer.tokenStart, lexer.tokenEnd), lexer.tokenStart, lexer.state)
            lexer.advance()
        }
        return result
    }

    @Test fun `separates property and parameter names and values`() {
        val actual = tokens("ATTENDEE;CN=Alex;ROLE=REQ-PARTICIPANT:mailto:alex@example.com")
        assertEquals(listOf(
            ICalTokenTypes.PROPERTY_KEY to "ATTENDEE",
            ICalTokenTypes.SEPARATOR to ";",
            ICalTokenTypes.PARAMETER_NAME to "CN",
            ICalTokenTypes.SEPARATOR to "=",
            ICalTokenTypes.PARAMETER_VALUE to "Alex",
            ICalTokenTypes.SEPARATOR to ";",
            ICalTokenTypes.PARAMETER_NAME to "ROLE",
            ICalTokenTypes.SEPARATOR to "=",
            ICalTokenTypes.PARAMETER_VALUE to "REQ-PARTICIPANT",
            ICalTokenTypes.SEPARATOR to ":",
            ICalTokenTypes.PROPERTY_VALUE to "mailto:alex@example.com"
        ), actual.map { it.type to it.text })
    }

    @Test fun `quoted parameter delimiters and lists stay in parameters`() {
        val actual = tokens("ATTENDEE;CN=\"Alex: Example; Team, A\";MEMBER=\"mailto:a@example.com\",\"mailto:b@example.com\":mailto:c@example.com")
        assertEquals(listOf("\"Alex: Example; Team, A\"", "\"mailto:a@example.com\"", "\"mailto:b@example.com\""),
            actual.filter { it.type == ICalTokenTypes.PARAMETER_VALUE }.map { it.text })
        assertEquals("mailto:c@example.com", actual.last().text)
        assertEquals(ICalTokenTypes.PROPERTY_VALUE, actual.last().type)
    }

    @Test fun `component keywords and names have separate tokens and ignore case`() {
        val actual = tokens("begin:VCALENDAR\r\nEND:VCALENDAR\nSUMMARY:BEGIN:still a value")
        assertEquals(listOf("begin", "END"), actual.filter { it.type == ICalTokenTypes.BLOCK_KEYWORD }.map { it.text })
        assertEquals(listOf("VCALENDAR", "VCALENDAR"), actual.filter { it.type == ICalTokenTypes.COMPONENT }.map { it.text })
        assertEquals(ICalTokenTypes.PROPERTY_VALUE, actual.last().type)
        assertEquals("BEGIN:still a value", actual.last().text)
    }

    @Test fun `folds preserve quoted parameters and property values while new lines reset state`() {
        val actual = tokens("ATTENDEE;CN=\"Alex\r\n Example: Team\":mailto:a@example.com\r\nDESCRIPTION:Hello\r\n world;literal:value\r\nSUMMARY:Next")
        assertEquals(listOf("\"Alex", "Example: Team\""), actual.filter { it.type == ICalTokenTypes.PARAMETER_VALUE }.map { it.text })
        assertEquals(listOf("mailto:a@example.com", "Hello", "world", "literal:value", "Next"),
            actual.filter { it.type == ICalTokenTypes.PROPERTY_VALUE }.map { it.text })
        assertEquals(listOf("ATTENDEE", "DESCRIPTION", "SUMMARY"), actual.filter { it.type == ICalTokenTypes.PROPERTY_KEY }.map { it.text })
    }

    @Test fun `vcard values keep structured text escaped characters and URIs intact`() {
        val actual = tokens("BEGIN:VCARD\nitem1.EMAIL;TYPE=work,home:alex@example.com\nN:Example;Alex;;;\nNOTE:Hello\\, world\\nNext\nEND:VCARD")
        assertEquals(listOf("work", "home"), actual.filter { it.type == ICalTokenTypes.PARAMETER_VALUE }.map { it.text })
        assertEquals(listOf("alex@example.com", "Example", "Alex", "Hello\\, world\\nNext"), actual.filter { it.type == ICalTokenTypes.PROPERTY_VALUE }.map { it.text })
    }

    @Test fun `structured values highlight semicolons including empty fields`() {
        val actual = tokens("N:Example;Alex;;;\nRRULE:FREQ=WEEKLY;BYDAY=MO,WE;COUNT=10")
        assertEquals(6, actual.count { it.type == ICalTokenTypes.VALUE_SEPARATOR })
        assertEquals(listOf("Example", "Alex", "FREQ=WEEKLY", "BYDAY=MO,WE", "COUNT=10"),
            actual.filter { it.type == ICalTokenTypes.PROPERTY_VALUE }.map { it.text })
    }

    @Test fun `escaped semicolons remain text and double backslashes allow separators`() {
        val actual = tokens("NOTE:one\\;two;three\\\\;four")
        assertEquals(listOf("one\\;two", "three\\\\", "four"),
            actual.filter { it.type == ICalTokenTypes.PROPERTY_VALUE }.map { it.text })
        assertEquals(2, actual.count { it.type == ICalTokenTypes.VALUE_SEPARATOR })
    }

    @Test fun `escape state and separators survive folding and lexer restarts`() {
        val text = "N:Example;Alex;;;\r\nNOTE:first\\\r\n ;literal;next\r\nRRULE:FREQ=DAILY;COUNT=2"
        val full = tokens(text)
        assertEquals(6, full.count { it.type == ICalTokenTypes.VALUE_SEPARATOR })
        assertTrue(full.any { it.type == ICalTokenTypes.PROPERTY_VALUE && it.text == ";literal" })
        full.forEachIndexed { index, token ->
            assertEquals(full.drop(index), tokens(text, token.offset, token.state))
        }
    }

    @Test fun `names can continue across a folded physical line`() {
        val actual = tokens("SU\r\n MMARY;LANG\r\n UAGE=en:Meeting")
        assertEquals(listOf("SU", "MMARY"), actual.filter { it.type == ICalTokenTypes.PROPERTY_KEY }.map { it.text })
        assertEquals(listOf("LANG", "UAGE"), actual.filter { it.type == ICalTokenTypes.PARAMETER_NAME }.map { it.text })
        assertEquals(ICalTokenTypes.PROPERTY_VALUE, actual.last().type)
    }

    @Test fun `restarting at every token restores the same highlighting`() {
        val text = "BEGIN:VEVENT\r\nDTSTART;TZID=Europe/Berlin:20261112T100000\r\nATTENDEE;CN=\"Alex\r\n Example: Team\";ROLE=REQ-PARTICIPANT:mailto:a@example.com\r\nDESCRIPTION:A\r\n folded value\r\nEND:VEVENT\r\n"
        val full = tokens(text)
        full.forEachIndexed { index, token ->
            assertEquals("Restart at ${token.offset}", full.drop(index), tokens(text, token.offset, token.state))
        }
    }

    @Test fun `empty and malformed input terminate and recover at a new line`() {
        assertTrue(tokens("").isEmpty())
        for (text in listOf(";=,:", "\r\n\n", "ATTENDEE;CN=\"unterminated\nSUMMARY:Next", "X;P=", "BEGIN:")) {
            val actual = tokens(text)
            assertEquals(text, actual.joinToString("") { it.text })
        }
        assertEquals(ICalTokenTypes.PROPERTY_VALUE, tokens("X;P=\"broken\nSUMMARY:Next").last().type)
    }

    @Test fun `highlighting has distinct keys for each requested category`() {
        val highlighter = ICalSyntaxHighlighter()
        val types = listOf(ICalTokenTypes.PROPERTY_KEY, ICalTokenTypes.PROPERTY_VALUE,
            ICalTokenTypes.PARAMETER_NAME, ICalTokenTypes.PARAMETER_VALUE, ICalTokenTypes.BLOCK_KEYWORD, ICalTokenTypes.COMPONENT, ICalTokenTypes.VALUE_SEPARATOR)
        assertEquals(types.size, types.map { highlighter.getTokenHighlights(it).single() }.toSet().size)
        assertTrue(highlighter.getTokenHighlights(TokenType.WHITE_SPACE).isEmpty())
    }
}
