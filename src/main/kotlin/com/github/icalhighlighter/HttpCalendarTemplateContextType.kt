package com.github.icalhighlighter

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType

class HttpCalendarTemplateContextType : TemplateContextType("HTTP request body (text/calendar)") {
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean =
        HttpTemplateContext.isInBody(templateActionContext, "text/calendar")
}
