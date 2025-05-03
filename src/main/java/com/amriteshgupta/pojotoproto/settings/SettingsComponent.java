package com.amriteshgupta.pojotoproto.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;


public class SettingsComponent {
    private final JPanel mainPanel;
    private final JBCheckBox generateSeparateFilesForNestedClassesCheckBox = new JBCheckBox("Generate separate files for nested classes");
    private final JComboBox<String> protoSyntaxComboBox = new JComboBox<>(new String[]{"proto2", "proto3"});
    private final TextFieldWithBrowseButton packagePathField = new TextFieldWithBrowseButton();

    public SettingsComponent(Project project) {

        mainPanel = FormBuilder.createFormBuilder()
                .addComponent(new JBLabel("Protocol Buffer Settings"))
                .addComponent(generateSeparateFilesForNestedClassesCheckBox)
                .addLabeledComponent(new JBLabel("Package Path For Generated Files:"), getPackageComponent(project))
                .addComponent(getInfoLabel())
                .addLabeledComponent(new JBLabel("Proto Syntax:"), protoSyntaxComboBox, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JComponent getInfoLabel() {
        JLabel infoLabel = new JBLabel("<html><i>If no path is specified, .proto files will be generated in the Scratch folder.</i></html>");
        infoLabel.setForeground(Color.GRAY);
        return infoLabel;
    }

    public JComponent getPackageComponent(Project project) {

        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        VirtualFile baseDir = project.getBaseDir();
        if (baseDir != null) descriptor.setRoots(baseDir);
        descriptor.setShowFileSystemRoots(false);

        packagePathField.addBrowseFolderListener(
                "Select Target Package Path",
                "Choose the project-relative path where .proto files should be generated",
                project,
                descriptor
        );
        packagePathField.setToolTipText("If no path is specified, the .proto files will be generated in the Scratch folder.");
        return packagePathField;
    }


    public JPanel getPanel() {
        return mainPanel;
    }

    @NotNull
    public Boolean isGenerateSeparateFilesForNestedClasses() {
        return generateSeparateFilesForNestedClassesCheckBox.isSelected();
    }

    public JBCheckBox getGenerateSeparateFilesForNestedClasses() {
        return generateSeparateFilesForNestedClassesCheckBox;
    }

    public void setGenerateSeparateFilesForNestedClasses(boolean generateSeparateFilesForNestedClasses) {
        generateSeparateFilesForNestedClassesCheckBox.setSelected(generateSeparateFilesForNestedClasses);
    }

    public String getPackagePath() {
        return packagePathField.getText();
    }

    public void setPackagePath(String packagePath) {
        packagePathField.setText(packagePath);
    }

    @NotNull
    public String getProtoSyntax() {
        return (String) protoSyntaxComboBox.getSelectedItem();
    }

    public void setProtoSyntax(String protoSyntax) {
        protoSyntaxComboBox.setSelectedItem(protoSyntax);
    }
}
