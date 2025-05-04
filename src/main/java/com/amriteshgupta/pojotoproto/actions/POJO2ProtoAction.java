package com.amriteshgupta.pojotoproto.actions;

import com.amriteshgupta.pojotoproto.parser.POJO2ProtoParser;
import com.amriteshgupta.pojotoproto.parser.POJO2ProtoParserFactory;
import com.amriteshgupta.pojotoproto.settings.SettingsState;
import com.amriteshgupta.pojotoproto.utils.Notifier;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.ide.scratch.ScratchRootType;
import com.intellij.json.JsonLanguage;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UastContextKt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

/**
 * Base action class for POJO to Proto conversion
 */
public abstract class POJO2ProtoAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        try {
            UElement uElement = getUElement(e);
            if (uElement == null) {
                Notifier.notify(project, "No valid Java class found", NotificationType.WARNING);
                return;
            }

            POJO2ProtoParser parser = POJO2ProtoParserFactory.createParser();
            Map<String, StringBuilder> protoToPackageName = parser.uElementToProtoString(uElement);
            SettingsState settings = SettingsState.getInstance();
            for(Map.Entry<String, StringBuilder> entry : protoToPackageName.entrySet()) {
                String protoContent = entry.getValue().toString();
                String className = entry.getKey().substring(entry.getKey().lastIndexOf('.') + 1);
                if(!settings.getSpecifiedProtoPackagePath().isEmpty()){
                    String specifiedProtoPackagePath = settings.getSpecifiedProtoPackagePath();
                    createProtoFileInSpecifiedPath(project, protoContent, specifiedProtoPackagePath, className);
                }
                else {
                    createProtoFileInScratch(project, protoContent, className);
                }
            }
            refreshPackageIfSpecified(settings.getSpecifiedProtoPackagePath());
            Notifier.notifyInfo(project, "Created proto files");

        } catch (Exception ex) {
            Notifier.notifyError(project, "Error: " + ex.getMessage());
        }
    }

    private void createProtoFileInSpecifiedPath(Project project, String protoContent, String specifiedProtoPackagePath,
                                                String className) {
        File dirFile = new File(specifiedProtoPackagePath);

        try {
            // Create directories if they don't exist
            if (!dirFile.exists() && !dirFile.mkdirs()) {
                Notifier.notifyError(project, "Failed to create directory: " + dirFile.getAbsolutePath());
                return;
            }
            File protoFile = new File(dirFile, className + ".proto");
            Files.write(protoFile.toPath(), protoContent.getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            Notifier.notifyError(project, "Error creating proto file: " + e.getMessage());
        }
    }

    private void createProtoFileInScratch(Project project, String protoContent, String className) {
        ScratchRootType.getInstance().createScratchFile(
                project,
                className + ".proto",
                JsonLanguage.INSTANCE,
                protoContent,
                ScratchFileService.Option.create_if_missing);
    }

    private void refreshPackageIfSpecified(String specifiedProtoPackagePath) {
        if(!specifiedProtoPackagePath.isEmpty()) {
            File dirFile = new File(specifiedProtoPackagePath);
            VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(dirFile);
            if (virtualFile != null) {
                virtualFile.refresh(false, true);
            }
        }
    }


    /**
     * Get UElement from action event
     *
     * @param e AnActionEvent
     * @return UElement
     */
    protected abstract UElement getUElement(@NotNull AnActionEvent e);

    /**
     * Get UElement from PsiElement
     *
     * @param psiElement PsiElement
     * @return UElement
     */
    protected UElement getUElementFromPsiElement(PsiElement psiElement) {
        if (psiElement == null) {
            return null;
        }

        // Try to get UClass directly
        UElement uElement = UastContextKt.toUElement(psiElement);
        if (uElement instanceof UClass) {
            return uElement;
        }

        // Try to find containing class
        PsiClass psiClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
        if (psiClass != null) {
            return UastContextKt.toUElement(psiClass);
        }

        return null;
    }

    /**
     * Get UElement from PsiFile
     *
     * @param psiFile PsiFile
     * @return UElement
     */
    protected UElement getUElementFromPsiFile(PsiFile psiFile) {
        if (psiFile instanceof PsiJavaFile) {
            PsiClass[] classes = ((PsiJavaFile) psiFile).getClasses();
            if (classes.length > 0) {
                return UastContextKt.toUElement(classes[0]);
            }
        }
        return null;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null && isAvailable(e));
    }

    /**
     * Check if action is available
     *
     * @param e AnActionEvent
     * @return true if action is available
     */
    protected boolean isAvailable(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        return psiFile instanceof PsiJavaFile;
    }
}
