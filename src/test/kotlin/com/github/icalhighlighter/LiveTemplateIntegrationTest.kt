package com.github.icalhighlighter

import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.codeInsight.template.impl.TemplateManagerImpl
import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.impl.TemplateSettings
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class LiveTemplateIntegrationTest : BasePlatformTestCase() {
    override fun setUp() {
        super.setUp()
        TemplateManagerImpl.setTemplateTesting(testRootDisposable)
    }

    fun testEventExpandsInCalendarFile() {
        myFixture.configureByText("calendar.ics", "BEGIN:VCALENDAR\n<caret>\nEND:VCALENDAR")
        assertSame(ICalFileType, myFixture.file.virtualFile.fileType)
        myFixture.type("icalevent")
        assertApplicable("icalevent", "iCalendar")
        myFixture.performEditorAction(IdeActions.ACTION_EXPAND_LIVE_TEMPLATE_BY_TAB)
        assertTrue("Event template did not expand: ${myFixture.editor.document.text}",
            myFixture.editor.document.text.contains("BEGIN:VEVENT"))
    }

    fun testContactExpandsInVCardFile() {
        myFixture.configureByText("contact.vcf", "<caret>")
        assertSame(VCardFileType, myFixture.file.virtualFile.fileType)
        myFixture.type("vcard")
        assertApplicable("vcard", "vCard")
        myFixture.performEditorAction(IdeActions.ACTION_EXPAND_LIVE_TEMPLATE_BY_TAB)
        assertTrue("Contact template did not expand: ${myFixture.editor.document.text}",
            myFixture.editor.document.text.contains("BEGIN:VCARD"))
    }

    fun testCalendarTemplateDoesNotExpandInVCardFile() {
        assertDoesNotExpand("contact.vcf", "icalevent")
    }

    fun testContactTemplateDoesNotExpandInCalendarFile() {
        assertDoesNotExpand("calendar.ics", "vcard")
    }

    fun testTemplatesDoNotExpandInPlainTextFiles() {
        assertDoesNotExpand("calendar.txt", "icalevent")
        assertDoesNotExpand("contact.txt", "vcard")
    }

    private fun assertDoesNotExpand(fileName: String, abbreviation: String) {
        myFixture.configureByText(fileName, "<caret>")
        myFixture.type(abbreviation)
        myFixture.performEditorAction(IdeActions.ACTION_EXPAND_LIVE_TEMPLATE_BY_TAB)
        assertEquals(abbreviation, myFixture.editor.document.text)
    }

    private fun assertApplicable(key: String, group: String) {
        val settings = TemplateSettings.getInstance()
        val template = requireNotNull(settings.getTemplate(key, group)) { "Missing template $group/$key" }
        val context = TemplateActionContext.expanding(myFixture.file, myFixture.editor)
        assertTrue("Template context=${template.templateContext}, applicable types=${TemplateManagerImpl.getApplicableContextTypes(context).map { it.contextId }}",
            TemplateManagerImpl.isApplicable(template, context))
        assertEquals('\t', settings.getShortcutChar(template))
        val manager = com.intellij.codeInsight.template.TemplateManager.getInstance(project) as TemplateManagerImpl
        val matching = manager.findMatchingTemplates(myFixture.file, myFixture.editor, '\t', settings)
        assertTrue("No matching template: $matching", matching.containsKey(template))
    }
}
