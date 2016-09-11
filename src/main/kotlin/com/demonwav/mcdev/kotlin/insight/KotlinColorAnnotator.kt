package com.demonwav.mcdev.kotlin.insight

import com.demonwav.mcdev.MinecraftSettings
import com.demonwav.mcdev.insight.ColorAnnotator
import com.demonwav.mcdev.platform.MinecraftModule
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.impl.compiled.ClsTypeElementImpl
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.getElementTextWithContext
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import java.awt.Color

class KotlinColorAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!MinecraftSettings.getInstance().isShowChatColorUnderlines) {
            return
        }

        if (element !is KtNameReferenceExpression) {
            return
        }

        val module = ModuleUtilCore.findModuleForPsiElement(element) ?: return
        val minecraftModule = MinecraftModule.getInstance(module) ?: return

        val ref = element.mainReference
        val res = ref.resolve()

        if (res !is PsiField) {
            return
        }

        if (res.typeElement !is ClsTypeElementImpl) {
            return
        }

        val typeElement = res.typeElement as ClsTypeElementImpl

        val qualifiedName = typeElement.canonicalText + "." + res.name

        var color: Color? = null
        loop@for (abstractModuleType in minecraftModule.types) {
            val map = abstractModuleType.classToColorMappings
            for ((key, value) in map) {
                if (key == qualifiedName.toString()) {
                    color = value
                    break@loop
                }
            }
        }

        if (color == null) {
            return
        }

        ColorAnnotator.setColorAnnotator(color, element.parent, holder)
    }
}
