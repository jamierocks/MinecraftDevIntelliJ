package com.demonwav.mcdev.kotlin.insight

import com.demonwav.mcdev.MinecraftSettings
import com.demonwav.mcdev.insight.ColorAnnotator
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement

class KotlinColorAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!MinecraftSettings.getInstance().isShowChatColorUnderlines) {
            return
        }

        val color = KotlinColorUtil.findColorFromElement(element) { it.value } ?: return

        ColorAnnotator.setColorAnnotator(color, element.parent, holder)
    }
}
