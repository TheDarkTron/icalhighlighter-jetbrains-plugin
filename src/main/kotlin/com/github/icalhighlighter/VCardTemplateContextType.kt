package com.github.icalhighlighter

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType

class VCardTemplateContextType : TemplateContextType("vCard") {
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean =
        // Template matching uses PSI copies; resolve their original file and its registered type.
        templateActionContext.file.originalFile.virtualFile?.fileType == VCardFileType
}
