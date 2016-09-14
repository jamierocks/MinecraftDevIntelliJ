package com.demonwav.mcdev.insight

import com.demonwav.mcdev.platform.MinecraftModule
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.impl.compiled.ClsTypeElementImpl
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import java.awt.Color

object KotlinColorUtil {

    fun findColorFromElement(element: PsiElement): Color? {
        if (element !is KtNameReferenceExpression) {
            return null
        }

        val module = ModuleUtilCore.findModuleForPsiElement(element) ?: return null
        val minecraftModule = MinecraftModule.getInstance(module) ?: return null

        val ref = element.mainReference
        val res = ref.resolve()

        if (res !is PsiField) {
            return null
        }

        val qualifiedName = if (res.typeElement is ClsTypeElementImpl) {
            val typeElement = res.typeElement as ClsTypeElementImpl
            typeElement.canonicalText + "." + res.name
        } else {
            res.type.canonicalText + "." + res.name
        }

        val entry = minecraftModule.types.asSequence().flatMap {
            it.classToColorMappings.asSequence()
        }.find {
            it.key == qualifiedName
        } ?: return null

        return entry.value
    }

    fun setColorTo(element: PsiElement, color: String) {
        try {
            WriteCommandAction.runWriteCommandAction(element.project) {
                val node = element.node
                val child = node.findChildByType(KtTokens.IDENTIFIER) ?: return@runWriteCommandAction

                val identifier = KtPsiFactory(element.project).createIdentifier(color)

                child.psi.replace(identifier)
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }
}
