package com.amriteshgupta.pojotoproto.actions;

import com.amriteshgupta.pojotoproto.utils.Notifier;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UastUtils;

/**
 * Action for editor popup menu
 */
public class EditorPopupMenuAction extends POJO2ProtoAction {

    @Override
    protected UElement getUElement(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        UElement uElementFromPsiElement = null;
        if (editor != null && psiFile != null) {
            int offset = editor.getCaretModel().getOffset();
            PsiElement element = psiFile.findElementAt(offset);
            uElementFromPsiElement = getUElementFromPsiElement(element);
        }

        if (uElementFromPsiElement == null) {
            String fileText = psiFile.getText();
            int offset = fileText.contains("class") ? fileText.indexOf("class") : fileText.indexOf("record");
            if (offset < 0) {
                Notifier.notifyWarning(e.getProject(),"Can't find class scope.");
                return null;
            }
            PsiElement elementAt = psiFile.findElementAt(offset);
            uElementFromPsiElement = UastUtils.findContaining(elementAt, UClass.class);
        }
        
        return uElementFromPsiElement;
    }
}
