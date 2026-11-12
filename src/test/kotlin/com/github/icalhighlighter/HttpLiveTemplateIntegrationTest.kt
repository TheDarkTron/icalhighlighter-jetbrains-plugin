package com.github.icalhighlighter

import com.intellij.codeInsight.template.impl.TemplateManagerImpl
import com.intellij.httpClient.http.request.psi.HttpRequest
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class HttpLiveTemplateIntegrationTest : BasePlatformTestCase() {
    override fun setUp() {
        super.setUp()
        TemplateManagerImpl.setTemplateTesting(testRootDisposable)
    }

    fun testEventExpandsInCalendarRequestBody() {
        expand("PUT https://example.invalid/calendar.ics\nContent-Type: text/calendar; charset=UTF-8\n\nBEGIN:VCALENDAR\n<caret>\nEND:VCALENDAR", "icalevent")
        assertTrue(myFixture.editor.document.text.contains("BEGIN:VEVENT"))
    }

    fun testCalendarExpandsInEmptyRequestBody() {
        expand("PUT https://example.invalid/calendar.ics\nContent-Type: text/calendar\n\n<caret>", "icalcalendar")
        assertTrue(myFixture.editor.document.text.contains("BEGIN:VCALENDAR"))
    }

    fun testContactExpandsInVCardRequestBody() {
        expand("PUT https://example.invalid/contact.vcf\ncontent-type: TEXT/VCARD; charset=utf-8\n\n<caret>", "vcard", "requests.rest")
        assertTrue(myFixture.editor.document.text.contains("BEGIN:VCARD"))
    }

    fun testEachRequestUsesItsOwnContentType() {
        val requests = "PUT https://example.invalid/calendar.ics\nContent-Type: text/calendar\n\nBEGIN:VCALENDAR\nEND:VCALENDAR\n\n### Contact\nPUT https://example.invalid/contact.vcf\nContent-Type: text/vcard\n\n<caret>"
        expand(requests, "vcard")
        assertTrue(myFixture.editor.document.text.contains("BEGIN:VCARD"))
    }

    fun testCalendarTemplateDoesNotExpandInVCardRequest() {
        assertDoesNotExpand("PUT https://example.invalid/contact.vcf\nContent-Type: text/vcard\n\n<caret>", "icalevent")
    }

    fun testVCardTemplateDoesNotExpandInCalendarRequest() {
        assertDoesNotExpand("PUT https://example.invalid/calendar.ics\nContent-Type: text/calendar\n\n<caret>", "vcard")
    }

    fun testTemplatesDoNotExpandInRequestHeaders() {
        assertDoesNotExpand("PUT https://example.invalid/calendar.ics\nContent-Type: text/calendar\nX-Example: <caret>\n\nBody", "icalevent")
    }

    fun testTemplatesDoNotExpandInOtherContentTypesOrMissingContentType() {
        assertDoesNotExpand("POST https://example.invalid\nContent-Type: application/json\n\n<caret>", "icalevent")
        assertDoesNotExpand("POST https://example.invalid\n\n<caret>", "vcard")
    }

    fun testTemplatesDoNotExpandInResponseScripts() {
        assertDoesNotExpand("POST https://example.invalid\nContent-Type: text/calendar\n\nBEGIN:VCALENDAR\nEND:VCALENDAR\n\n> {%\n<caret>\n%}", "icalevent")
    }

    private fun assertDoesNotExpand(text: String, abbreviation: String) {
        expand(text, abbreviation)
        assertEquals(text.replace("<caret>", abbreviation), myFixture.editor.document.text)
    }

    private fun expand(text: String, abbreviation: String, fileName: String = "requests.http") {
        myFixture.configureByText(fileName, text)
        assertNotNull("HTTP Client PSI must be active", PsiTreeUtil.findChildOfType(myFixture.file, HttpRequest::class.java))
        myFixture.type(abbreviation)
        myFixture.performEditorAction(IdeActions.ACTION_EXPAND_LIVE_TEMPLATE_BY_TAB)
    }
}
