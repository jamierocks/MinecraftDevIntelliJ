package com.demonwav.mcdev.scala.insight

import com.demonwav.mcdev.MinecraftSettings
import com.demonwav.mcdev.insight.ColorAnnotator
import com.demonwav.mcdev.platform.MinecraftModule
import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.impl.compiled.{ClsFieldImpl, ClsTypeElementImpl}
import com.intellij.psi.{PsiElement, PsiField}
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScReferenceExpression

import scala.collection.JavaConversions._

class ScalaColorAnnotator extends Annotator {

    override def annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!MinecraftSettings.getInstance().isShowChatColorUnderlines) {
            return
        }

        if (!element.isInstanceOf[ScReferenceExpression]) {
            return
        }

        val module = ModuleUtilCore.findModuleForPsiElement(element)
        if (module == null) {
            return
        }

        val minecraftModule = MinecraftModule.getInstance(module)
        if (minecraftModule == null) {
            return
        }

        val ref = element.getReference
        val res = ref.resolve()

        if (!res.isInstanceOf[PsiField]) {
            return
        }

        if (!res.isInstanceOf[ClsFieldImpl]) {
            return
        }

        val field = res.asInstanceOf[ClsFieldImpl]
        if (!field.getTypeElement.isInstanceOf[ClsTypeElementImpl]) {
            return
        }

        val typeElement = field.getTypeElement.asInstanceOf[ClsTypeElementImpl]

        val qualifiedName = typeElement.getCanonicalText concat "." concat field.getName

        val color = minecraftModule.getTypes.toStream
            .map(abstractModuleType => abstractModuleType.getClassToColorMappings)
            .flatMap(map => map.toMap)
            .find(t => t._1 == qualifiedName)

        if (color.isEmpty) {
            return
        }

        ColorAnnotator.setColorAnnotator(color.get._2, element, holder)
    }
}
