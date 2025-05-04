package com.amriteshgupta.pojotoproto.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UElement;

/**
 * Action for project view popup menu
 */
public class ProjectViewPopupMenuAction extends POJO2ProtoAction {

    @Override
    protected UElement getUElement(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        return psiFile != null ? getUElementFromPsiFile(psiFile) : null;
    }
}
