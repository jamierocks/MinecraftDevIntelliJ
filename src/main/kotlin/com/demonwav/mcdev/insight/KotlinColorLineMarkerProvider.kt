package com.demonwav.mcdev.insight

import com.demonwav.mcdev.MinecraftSettings
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.daemon.NavigateAction
import com.intellij.psi.PsiElement
import java.util.function.BiConsumer

class KotlinColorLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (!MinecraftSettings.getInstance().isShowChatColorGutterIcons) {
            return null
        }

        val color = KotlinColorUtil.findColorFromElement(element) ?: return null
        val info = ColorLineMarkerProvider.ColorInfo(element, color, BiConsumer { element, newColor ->
                KotlinColorUtil.setColorTo(element, newColor)
            })

        NavigateAction.setNavigateAction(info, "Change color", null)

        return info
    }

    override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {}
}
