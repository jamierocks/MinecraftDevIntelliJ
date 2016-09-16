package com.demonwav.mcdev.insight

import com.demonwav.mcdev.MinecraftSettings
import com.demonwav.mcdev.platform.MinecraftModule
import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.{PsiClass, PsiElement, PsiParameter}

import scala.collection.JavaConversions._

class ScalaListenerEventAnnotator extends Annotator {

    override def annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!MinecraftSettings.getInstance().isShowEventListenerGutterIcons) {
            return
        }

        val module = ModuleUtilCore.findModuleForPsiElement(element)
        if (module == null) {
            return
        }

        val instance = MinecraftModule.getInstance(module)
        if (instance == null) {
            return
        }

        var parameter: PsiParameter = null
        var eventClass: PsiElement = null
        var annotation: String = null
        try {
            var error = false
            val (anyClass, function) = ScalaInsightUtil.getEventListenerForElement(element)
            eventClass = anyClass
            if (!anyClass.isInstanceOf[PsiClass]) {
                // Don't throw exception here because we need to get the annotation and parameter first
                error = true
            }

            val modifierList = function.getModifierList
            if (modifierList == null) {
                return
            }

            val annotationOption = instance.getTypes.toStream.filterNot(m => m == null).flatMap(m => m.getListenerAnnotations).find(listenerAnnotation => {
                modifierList.findAnnotation(listenerAnnotation) != null
            })
            if (annotationOption == null || annotationOption.isEmpty) {
                return
            }

            annotation = annotationOption.get

            val parameters = function.parameterList.getParameters()
            if (parameters.length < 1) {
                return
            }

            parameter = parameters(0)

            if (error) {
                throw new Exception
            }

            if (instance.isEventClassValid(eventClass, annotation)) {
                return
            }

            if (!ListenerEventAnnotator.isSuperEventListenerAllowed(eventClass, annotation, instance)) {
                throw new Exception
            }
        } catch {
            case e: Exception =>
                if (parameter != null && eventClass != null && annotation != null) {
                    holder.createErrorAnnotation(parameter, instance.writeErrorMessageForEvent(eventClass, annotation))
                }
        }
    }
}
