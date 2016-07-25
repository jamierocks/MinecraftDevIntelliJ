package com.demonwav.mcdev.util;

import com.google.common.collect.ImmutableSet;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.java.IKeywordElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class McPsiUtil {

    @Nullable
    public static PsiClass getClassOfElement(@NotNull PsiElement element) {
        if (element instanceof PsiClass) {
            return (PsiClass) element;
        }

        while (element.getParent() != null) {

            if (element.getParent() instanceof PsiClass) {
                return (PsiClass) element.getParent();
            }

            if (element.getParent() instanceof PsiFile || element.getParent() instanceof PsiDirectory) {
                return null;
            }

            element = element.getParent();
        }
        return null;
    }

    private static final ImmutableSet<String> METHOD_ACCESS_MODIFIERS = ImmutableSet.<String>builder()
            .add(PsiModifier.PUBLIC)
            .add(PsiModifier.PROTECTED)
            .add(PsiModifier.PACKAGE_LOCAL)
            .add(PsiModifier.PRIVATE)
            .build();

    public static String getMethodAccessModifier(PsiMethod method) {
        return METHOD_ACCESS_MODIFIERS.stream()
                .filter(method::hasModifierProperty)
                .findFirst()
                .orElse(PsiModifier.PUBLIC);
    }

    public static IElementType getMethodAccessType(PsiMethod method) {
        for (PsiElement modifier : method.getModifierList().getChildren()) {
            if (modifier instanceof PsiKeyword) {
                final IElementType tokenType = ((PsiKeyword) modifier).getTokenType();

            }
        }
        return JavaTokenType.PUBLIC_KEYWORD;
    }
}
