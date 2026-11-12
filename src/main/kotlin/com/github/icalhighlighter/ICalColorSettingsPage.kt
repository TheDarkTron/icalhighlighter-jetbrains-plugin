package com.github.icalhighlighter

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class ICalColorSettingsPage : ColorSettingsPage {
    override fun getDisplayName(): String = "iCal / vCard"
    override fun getIcon(): Icon = ICalFileType.icon
    override fun getHighlighter(): SyntaxHighlighter = ICalSyntaxHighlighter()
    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = arrayOf(
        AttributesDescriptor("Property name", ICalSyntaxHighlighter.PROPERTY_KEY),
        AttributesDescriptor("Property value", ICalSyntaxHighlighter.PROPERTY_VALUE),
        AttributesDescriptor("Parameter name", ICalSyntaxHighlighter.PARAMETER_NAME),
        AttributesDescriptor("Parameter value", ICalSyntaxHighlighter.PARAMETER_VALUE),
        AttributesDescriptor("BEGIN / END keyword", ICalSyntaxHighlighter.BLOCK_KEYWORD),
        AttributesDescriptor("Component name", ICalSyntaxHighlighter.COMPONENT),
        AttributesDescriptor("Separator", ICalSyntaxHighlighter.SEPARATOR),
        AttributesDescriptor("Property value separator (;)", ICalSyntaxHighlighter.VALUE_SEPARATOR),
        AttributesDescriptor("Invalid character", ICalSyntaxHighlighter.BAD_CHARACTER)
    )

    override fun getDemoText(): String = """
        BEGIN:VCALENDAR
        VERSION:2.0
        PRODID:-//iCal Highlighter//Demo//EN
        BEGIN:VEVENT
        UID:demo@example.com
        DTSTART;TZID=Europe/Berlin:20261112T100000
        ATTENDEE;CN="Alex Example";ROLE=REQ-PARTICIPANT:mailto:alex@example.com
        SUMMARY:Planning meeting
        RRULE:FREQ=WEEKLY;BYDAY=MO,WE;COUNT=10
        DESCRIPTION:A long description that is
         continued on a folded line.
        END:VEVENT
        END:VCALENDAR

        BEGIN:VCARD
        VERSION:4.0
        FN:Alex Example
        N:Example;Alex;;;
        ADR;TYPE=work:;;Example Street 1;Berlin;;10115;Germany
        EMAIL;TYPE=work:alex@example.com
        END:VCARD
    """.trimIndent()
}
