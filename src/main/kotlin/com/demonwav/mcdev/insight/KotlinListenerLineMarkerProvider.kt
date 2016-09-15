package com.demonwav.mcdev.insight

import com.demonwav.mcdev.MinecraftSettings
import com.demonwav.mcdev.asset.PlatformAssets
import com.intellij.codeHighlighting.Pass
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.psi.PsiElement
import javax.swing.Icon

class KotlinListenerLineMarkerProvider : LineMarkerProviderDescriptor() {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (!MinecraftSettings.getInstance().isShowEventListenerGutterIcons) {
            return null
        }

        val (any, function) = element.getEventListener() ?: return null

        return ListenerLineMarkerProvider.EventLineMarkerInfo(
            element,
            element.textRange,
            icon,
            Pass.UPDATE_ALL,
            function.handler
        )
    }

    override fun getName(): String {
        return "Event Listener line marker"
    }

    override fun collectSlowLineMarkers(elements: List<PsiElement>, result: Collection<LineMarkerInfo<*>>) {}

    override fun getIcon(): Icon {
        return PlatformAssets.LISTENER
    }
}