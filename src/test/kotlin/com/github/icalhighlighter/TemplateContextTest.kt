package com.github.icalhighlighter

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.psi.PsiFile
import com.intellij.testFramework.LightVirtualFile
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.reflect.Proxy

class TemplateContextTest {
    private fun context(fileType: FileType): TemplateActionContext {
        val virtualFile = LightVirtualFile("test.${fileType.defaultExtension}", fileType, "")
        val file = Proxy.newProxyInstance(PsiFile::class.java.classLoader, arrayOf(PsiFile::class.java)) { psiFile, method, _ ->
            when (method.name) {
                "getOriginalFile" -> psiFile
                "getVirtualFile" -> virtualFile
                "getTextLength" -> 0
                else -> error("Unexpected PsiFile call: ${method.name}")
            }
        } as PsiFile
        return TemplateActionContext.expanding(file, 0)
    }

    @Test fun `calendar templates are allowed only in calendar files`() {
        val calendar = ICalTemplateContextType()
        for (fileType in listOf(ICalFileType, VCardFileType, PlainTextFileType.INSTANCE)) {
            assertEquals(fileType == ICalFileType, calendar.isInContext(context(fileType)))
        }
    }

    @Test fun `contact templates are allowed only in contact files`() {
        val contact = VCardTemplateContextType()
        for (fileType in listOf(ICalFileType, VCardFileType, PlainTextFileType.INSTANCE)) {
            assertEquals(fileType == VCardFileType, contact.isInContext(context(fileType)))
        }
    }
}
