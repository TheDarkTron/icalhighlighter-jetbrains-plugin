package com.github.icalhighlighter

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType

class HttpVCardTemplateContextType : TemplateContextType("HTTP request body (text/vcard)") {
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean =
        HttpTemplateContext.isInBody(templateActionContext, "text/vcard")
}
