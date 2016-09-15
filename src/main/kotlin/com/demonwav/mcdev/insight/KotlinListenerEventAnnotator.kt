package com.demonwav.mcdev.insight

import com.demonwav.mcdev.MinecraftSettings
import com.demonwav.mcdev.platform.MinecraftModule
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.renderer.render

class KotlinListenerEventAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!MinecraftSettings.getInstance().isShowEventListenerGutterIcons) {
            return
        }

        val module = ModuleUtilCore.findModuleForPsiElement(element) ?: return
        val instance = MinecraftModule.getInstance(module) ?: return

        var parameter: KtParameter? = null
        var eventClass: PsiElement? = null
        var annotation: String? = null
        try {
            val (anyClass, function) = element.getEventListener() ?: return
            eventClass = anyClass
            var error = false
            if (anyClass is KtClassOrObject) {
                // Don't throw exception here because we need to get the annotation and parameter first
                error = true
            }

            val modifierList = function.modifierList ?: return

            annotation = instance.types.filterNotNull().flatMap { it.listenerAnnotations }.find { listenerAnnotation ->
                modifierList.annotationEntries.find {
                    it?.calleeExpression
                            ?.constructorReferenceExpression
                            ?.mainReference
                            ?.resolve()
                            ?.getKotlinFqName()
                            ?.render() == listenerAnnotation
                } != null
            } ?: return

            val parameters = function.valueParameters
            if (parameters.size < 1) {
                return
            }

            parameter = parameters[0]

            if (error) {
                throw Exception()
            }

            if (!instance.isEventClassValid(eventClass, annotation)) {
                throw Exception()
            }

            if (!ListenerEventAnnotator.isSuperEventListenerAllowed(eventClass, annotation, instance)) {
                throw Exception()
            }
        } catch (e: Exception) {
            if (parameter != null && eventClass != null && annotation != null){
                holder.createErrorAnnotation(parameter, instance.writeErrorMessageForEvent(eventClass, annotation))
            }
        }
    }
}
