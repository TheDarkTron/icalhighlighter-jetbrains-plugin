package com.github.icalhighlighter

import com.intellij.psi.tree.IElementType

object ICalTokenTypes {
    val PROPERTY_KEY = ICalElementType("PROPERTY_KEY")
    val PROPERTY_VALUE = ICalElementType("PROPERTY_VALUE")
    val PARAMETER_NAME = ICalElementType("PARAMETER_NAME")
    val PARAMETER_VALUE = ICalElementType("PARAMETER_VALUE")
    val BLOCK_KEYWORD = ICalElementType("BLOCK_KEYWORD")
    val COMPONENT = ICalElementType("COMPONENT")
    val SEPARATOR = ICalElementType("SEPARATOR")
    val VALUE_SEPARATOR = ICalElementType("VALUE_SEPARATOR")
}
