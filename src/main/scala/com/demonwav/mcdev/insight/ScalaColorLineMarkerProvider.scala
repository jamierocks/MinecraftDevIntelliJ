package com.demonwav.mcdev.insight

import java.util
import java.util.function.BiConsumer

import com.demonwav.mcdev.MinecraftSettings
import com.intellij.codeInsight.daemon.{LineMarkerInfo, LineMarkerProvider, NavigateAction}
import com.intellij.psi.PsiElement

class ScalaColorLineMarkerProvider extends LineMarkerProvider {
    override def getLineMarkerInfo(element: PsiElement): LineMarkerInfo[_] = {
        if (!MinecraftSettings.getInstance().isShowChatColorGutterIcons) {
            return null
        }

        val color = ScalaColorUtil.findColorFromElement(element)
        if (color == null) {
            return null
        }

        val info = new ColorLineMarkerProvider.ColorInfo(element, color, new BiConsumer[PsiElement, String] {
            override def accept(t: PsiElement, u: String) = {
                ScalaColorUtil.setColorTo(t, u)
            }
        })

        NavigateAction.setNavigateAction(info, "Change color", null)

        info
    }

    override def collectSlowLineMarkers(elements: util.List[PsiElement], result: util.Collection[LineMarkerInfo[_ <: PsiElement]]): Unit = {}
}
