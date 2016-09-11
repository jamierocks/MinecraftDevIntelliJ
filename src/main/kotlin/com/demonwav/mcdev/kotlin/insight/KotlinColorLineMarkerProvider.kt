package com.demonwav.mcdev.kotlin.insight

import com.demonwav.mcdev.MinecraftSettings
import com.demonwav.mcdev.insight.ColorLineMarkerProvider
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

        val info = KotlinColorUtil.findColorFromElement(element) { chosen ->
            ColorLineMarkerProvider.ColorInfo(element, chosen.value, BiConsumer { element, newColor ->
                KotlinColorUtil.setColorTo(element, newColor)
            })
        }

        if (info != null) {
            NavigateAction.setNavigateAction(info, "Change color", null)
        }

        return info
    }

    override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {}
}
