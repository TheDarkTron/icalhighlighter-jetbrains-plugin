package com.github.icalhighlighter

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.httpClient.http.request.psi.HttpMessageBody
import com.intellij.httpClient.http.request.psi.HttpRequest
import com.intellij.psi.util.PsiTreeUtil

internal object HttpTemplateContext {
    fun isInBody(context: TemplateActionContext, mediaType: String): Boolean {
        val file = context.file
        if (file.textLength == 0) return false
        val element = file.findElementAt(context.startOffset.coerceIn(0, file.textLength - 1)) ?: return false
        val body = PsiTreeUtil.getParentOfType(element, HttpMessageBody::class.java, false) ?: return false
        val request = PsiTreeUtil.getParentOfType(body, HttpRequest::class.java) ?: return false
        val contentType = request.getHeaderField("Content-Type")?.headerFieldValue?.text ?: return false
        return contentType.substringBefore(';').trim().equals(mediaType, ignoreCase = true)
    }
}
