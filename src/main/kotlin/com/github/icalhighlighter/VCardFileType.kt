package com.github.icalhighlighter

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object VCardFileType : LanguageFileType(VCardLanguage) {
    override fun getName(): String = "vCard File"
    override fun getDescription(): String = "vCard contact file"
    override fun getDefaultExtension(): String = "vcf"
    override fun getIcon(): Icon = IconLoader.getIcon("/icons/vcard.svg", VCardFileType::class.java)
}
