package com.demonwav.mcdev.insight

import com.demonwav.mcdev.MinecraftSettings
import com.intellij.codeHighlighting.Pass
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.psi.{PsiClass, PsiElement}
import org.jetbrains.plugins.scala.lang.psi.api.statements.ScFunctionDefinition

class ScalaListenerLineMarkerProvider extends ListenerLineMarkerProvider {

    override def getLineMarkerInfo(element: PsiElement): LineMarkerInfo[_] = {
        if (!MinecraftSettings.getInstance().isShowEventListenerGutterIcons) {
            return null
        }

        val tuple2 = ScalaInsightUtil.getEventListenerForElement(element)
        if (tuple2 == null) {
            return null
        }

        new ListenerLineMarkerProvider.EventLineMarkerInfo(
            element,
            element.getTextRange,
            getIcon,
            Pass.UPDATE_ALL,
            ScalaInsightUtil.getHandler(tuple2._2)
        )
    }
}
