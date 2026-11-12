package com.github.icalhighlighter

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object ICalFileType : LanguageFileType(ICalLanguage.INSTANCE) {
    override fun getName(): String = "iCal File"
    override fun getDescription(): String = "iCal calendar file"
    override fun getDefaultExtension(): String = "ics"
    override fun getIcon(): Icon = IconLoader.getIcon("/icons/ical.svg", ICalFileType::class.java)
} 
