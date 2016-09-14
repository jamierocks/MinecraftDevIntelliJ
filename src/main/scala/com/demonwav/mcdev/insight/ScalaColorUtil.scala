package com.demonwav.mcdev.insight

import java.awt.Color

import com.demonwav.mcdev.platform.MinecraftModule
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.compiled.{ClsFieldImpl, ClsTypeElementImpl}
import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenTypes
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScReferenceExpression
import org.jetbrains.plugins.scala.lang.psi.impl.ScalaPsiElementFactory

import scala.collection.JavaConversions._


object ScalaColorUtil {

    def findColorFromElement[T](element: PsiElement): Color = {
        if (!element.isInstanceOf[ScReferenceExpression]) {
            return null
        }

        val module = ModuleUtilCore.findModuleForPsiElement(element)
        if (module == null) {
            return null
        }

        val minecraftModule = MinecraftModule.getInstance(module)
        if (minecraftModule == null) {
            return null
        }

        val ref = element.getReference
        val res = ref.resolve()

        if (!res.isInstanceOf[ClsFieldImpl]) {
            return null
        }

        val field = res.asInstanceOf[ClsFieldImpl]
        if (!field.getTypeElement.isInstanceOf[ClsTypeElementImpl]) {
            return null
        }

        val typeElement = field.getTypeElement.asInstanceOf[ClsTypeElementImpl]

        val qualifiedName = typeElement.getCanonicalText + "." + field.getName

        val color = minecraftModule.getTypes.toStream
            .flatMap(abstractModuleType => abstractModuleType.getClassToColorMappings.toMap)
            .find(t => t._1 == qualifiedName)

        if (color.isEmpty) {
            return null
        }

        color.get._2
    }

    def setColorTo(element: PsiElement, color: String) = {
        try {
            WriteCommandAction.runWriteCommandAction(element.getProject, new Runnable {
                override def run(): Unit = {
                    val node = element.getNode
                    val child = node.findChildByType(ScalaTokenTypes.IDENTIFIER_TOKEN_SET)
                    if (child == null) {
                        return
                    }

                    val identifier = ScalaPsiElementFactory.createIdentifier(color, element.getManager)

                    child.getPsi.replace(identifier.getPsi)
                }
            })
        } catch {
            case throwable: Throwable => throwable.printStackTrace()
        }
    }
}
