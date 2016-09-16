package com.demonwav.mcdev.insight

import java.awt.event.MouseEvent

import com.demonwav.mcdev.platform.MinecraftModule
import com.intellij.codeInsight.TargetElementUtil
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.featureStatistics.FeatureUsageTracker
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.impl.source.PsiImmediateClassType
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.{PsiClass, PsiElement}
import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenTypes
import org.jetbrains.plugins.scala.lang.psi.api.statements.ScFunctionDefinition
import org.jetbrains.plugins.scala.lang.psi.impl.statements.params.ScParameterImpl

import scala.collection.JavaConversions._

object ScalaInsightUtil {

    def getEventListenerForElement(element: PsiElement): (PsiElement, ScFunctionDefinition) = {
        if (!element.isInstanceOf[LeafPsiElement] ||
            !element.getParent.isInstanceOf[ScFunctionDefinition] ||
            element.asInstanceOf[LeafPsiElement].getElementType != ScalaTokenTypes.tIDENTIFIER) {
            return null
        }

        val function = element.getParent.asInstanceOf[ScFunctionDefinition]

        if (function.isPrivate ||
            function.isProtected ||
            function.isLocal ||
            function.isAbstractMember ||
            function.isExtensionMethod ||
            function.containingClass.getModifierList.hasModifierProperty("object")) {
            // TODO Forge allows static listeners
            return null
        }

        val modifierList = function.getModifierList
        val module = ModuleUtilCore.findModuleForPsiElement(element)
        if (module == null) {
            return null
        }

        val instance = MinecraftModule.getInstance(module)
        if (instance == null) {
            return null
        }

        val listenerAnnotations = instance.getTypes.toSeq.flatMap(t => t.getListenerAnnotations)

        if (!listenerAnnotations.exists(annotation => modifierList.findAnnotation(annotation) != null)) {
            return null
        }

        val parameters = function.parameterList.getParameters()
        if (parameters.length < 1) {
            return null
        }

        val parameter = parameters(0)
        if (!parameter.isInstanceOf[ScParameterImpl]) {
            return null
        }

        // parameter.getType.asInstanceOf[PsiImmediateClassType].resolve()
        val parameterType = parameter.getType
        if (!parameterType.isInstanceOf[PsiImmediateClassType]) {
            return null
        }

        val resolve = parameterType.asInstanceOf[PsiImmediateClassType].resolve()

        (resolve, function)
    }

    def getEventParameterTupleForFunction(function: ScFunctionDefinition): (PsiImmediateClassType, PsiClass) = {
        val parameterTypes = function.parameterList.getParameters().toSeq
            .map(p => p.getType)
            .filter(t => t.isInstanceOf[PsiImmediateClassType])
            .map(t => t.asInstanceOf[PsiImmediateClassType]).toList

        if (parameterTypes.length < 1) {
            return null
        }

        val parameterType = parameterTypes.head
        if (parameterType == null) {
            return null
        }

        val resolve = parameterType.resolve()
        if (resolve == null) {
            return null
        }

        (parameterType, resolve)
    }

    def getHandler(function: ScFunctionDefinition): GutterIconNavigationHandler[PsiElement] =
        new GutterIconNavigationHandler[PsiElement] {
            override def navigate(e: MouseEvent, elt: PsiElement): Unit = {
                val containingFile = elt.getContainingFile
                if (containingFile == null) {
                    return
                }

                val (typeElement, resolve) = getEventParameterTupleForFunction(function)

                val editor = FileEditorManager.getInstance(function.getProject).getSelectedTextEditor
                if (editor == null) {
                    return
                }

                FeatureUsageTracker.getInstance().triggerFeatureUsed("navigation.goto.declaration")
                var navElement = resolve.getNavigationElement
                navElement = TargetElementUtil.getInstance().getGotoDeclarationTarget(resolve, navElement)

                if (navElement != null) {
                    ListenerLineMarkerProvider.gotoTargetElement(navElement, editor, containingFile)
                }
            }
        }
}
