package com.github.icalhighlighter

import com.intellij.psi.tree.IElementType

object ICalTokenTypes {
    val PROPERTY_KEY = ICalElementType("PROPERTY_KEY")
    val PROPERTY_VALUE = ICalElementType("PROPERTY_VALUE")
    val COMPONENT = ICalElementType("COMPONENT")
    val SEPARATOR = ICalElementType("SEPARATOR")
} 