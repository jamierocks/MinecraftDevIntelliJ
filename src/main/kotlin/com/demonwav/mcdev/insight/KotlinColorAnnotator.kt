package com.demonwav.mcdev.insight

import com.demonwav.mcdev.MinecraftSettings
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement

class KotlinColorAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!MinecraftSettings.getInstance().isShowChatColorUnderlines) {
            return
        }

        val color = element.findColor() ?: return

        ColorAnnotator.setColorAnnotator(color, element.parent, holder)
    }
}
