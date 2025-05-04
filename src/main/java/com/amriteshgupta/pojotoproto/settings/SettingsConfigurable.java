package com.amriteshgupta.pojotoproto.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


public class SettingsConfigurable implements Configurable {
    private SettingsComponent settingsComponent;
    private final Project project;

    public SettingsConfigurable(Project project) {
        this.project = project;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "POJO To Proto";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return settingsComponent.getGenerateSeparateFilesForNestedClasses();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        settingsComponent = new SettingsComponent(project);
        return settingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        SettingsState settings = SettingsState.getInstance();
        return !settingsComponent.isGenerateSeparateFilesForNestedClasses().equals(settings.isGenerateSeparateFilesForNestedClasses()) ||
                !settingsComponent.getPackagePath().equals(settings.getSpecifiedProtoPackagePath()) ||
                !settingsComponent.getProtoSyntax().equals(settings.getProtoSyntax());
    }

    @Override
    public void apply() {
        SettingsState settings = SettingsState.getInstance();
        settings.setGenerateSeparateFilesForNestedClasses(settingsComponent.isGenerateSeparateFilesForNestedClasses());
        settings.setSpecifiedProtoPackagePath(settingsComponent.getPackagePath());
        settings.setProtoSyntax(settingsComponent.getProtoSyntax());
    }

    @Override
    public void reset() {
        SettingsState settings = SettingsState.getInstance();
        settingsComponent.setGenerateSeparateFilesForNestedClasses(settings.isGenerateSeparateFilesForNestedClasses());
        settingsComponent.setPackagePath(settings.getSpecifiedProtoPackagePath());
        settingsComponent.setProtoSyntax(settings.getProtoSyntax());
    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }
}
