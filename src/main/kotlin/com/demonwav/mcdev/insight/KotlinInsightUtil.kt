@file:JvmName("KotlinInsightUtil")
package com.demonwav.mcdev.insight

import com.demonwav.mcdev.platform.MinecraftModule
import com.intellij.codeInsight.TargetElementUtil
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.featureStatistics.FeatureUsageTracker
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isObjectLiteral
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes
import org.jetbrains.kotlin.renderer.render

fun PsiElement.getEventListener(): Pair<PsiElement, KtNamedFunction>? {
    if (this !is LeafPsiElement || this.parent !is KtNamedFunction || this.elementType != KtTokens.IDENTIFIER) {
        return null
    }

    val function = this.parent as KtNamedFunction

    // Effectively checking if it's static
    if (function.isTopLevel ||
        function.containingClassOrObject?.isObjectLiteral() == true ||
        function.hasModifier(KtTokens.ABSTRACT_KEYWORD) ||
        function.hasModifier(KtTokens.PRIVATE_KEYWORD)) {
        // Hopefully this works. Needs to handle Forge allowing static listeners
        return null
    }

    val modifierList = function.modifierList
    val module = ModuleUtilCore.findModuleForPsiElement(this) ?: return null

    val instance = MinecraftModule.getInstance(module) ?: return null

    val listenerAnnotations = instance.types.flatMap { it.listenerAnnotations }

    var contains = false
    for (listenerAnnotation in listenerAnnotations) {

        if (modifierList?.annotationEntries?.find {
            it?.calleeExpression
                ?.constructorReferenceExpression
                ?.mainReference
                ?.resolve()
                ?.getKotlinFqName()
                ?.render() == listenerAnnotation
        } != null) {
            contains = true
            break
        }
    }

    if (!contains) {
        return null
    }

    val parameters = function.valueParameters
    if (parameters.size < 1) {
        return null
    }

    val parameter = parameters[0] ?: return null

    val userType = parameter.typeReference?.typeElement as KtUserType? ?: return null
    val resolve = userType.referenceExpression?.mainReference?.resolve() ?: return null

    return resolve to function
}

fun KtNamedFunction.getEventParameterPair(): Pair<KtTypeReference, PsiClass>? {
    val parameters = valueParameters.map { it.typeReference }
    if (parameters.size < 1) {
        return null
    }

    val parameter = parameters[0] ?: return null

    val type = parameter.node
        .findChildByType(KtStubElementTypes.USER_TYPE)
        ?.findChildByType(KtStubElementTypes.REFERENCE_EXPRESSION)
        ?.psi as KtNameReferenceExpression? ?: return null

    val resolve = type.mainReference.resolve() as? PsiClass? ?: return null

    return parameter to resolve
}

val KtNamedFunction.handler: GutterIconNavigationHandler<PsiElement>
    get() = GutterIconNavigationHandler { e, element1 ->
        val containingFile = element1.containingFile ?: return@GutterIconNavigationHandler
        val (type, resolve) = getEventParameterPair() ?: return@GutterIconNavigationHandler

        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return@GutterIconNavigationHandler

        FeatureUsageTracker.getInstance().triggerFeatureUsed("navigation.goto.declaration")
        var navElement = resolve.navigationElement
        navElement = TargetElementUtil.getInstance().getGotoDeclarationTarget(resolve, navElement)

        if (navElement != null) {
            ListenerLineMarkerProvider.gotoTargetElement(navElement, editor, containingFile)
        }

    }