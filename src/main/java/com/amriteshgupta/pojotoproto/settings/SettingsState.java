package com.amriteshgupta.pojotoproto.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@State(
        name = "com.github.amriteshgupta.pojotoproto.settings.SettingsState",
        storages = @Storage("PojoToProtoSettings.xml")
)
public class SettingsState implements PersistentStateComponent<SettingsState> {

    private Map<String, String> javaToProtoTypeMap = new HashMap<>();
    private boolean generateSeparateFilesForNestedClasses = false;
    private String specifiedProtoPackagePath = "";
    private String protoSyntax = "proto3";

    public SettingsState() {
        initDefaultMappings();
    }

    private void initDefaultMappings() {
        // Initialize default Java to Protocol Buffer type mappings
        javaToProtoTypeMap.put("java.lang.String", "string");
        javaToProtoTypeMap.put("java.lang.Boolean", "bool");
        javaToProtoTypeMap.put("boolean", "bool");
        javaToProtoTypeMap.put("java.lang.Integer", "int32");
        javaToProtoTypeMap.put("int", "int32");
        javaToProtoTypeMap.put("java.lang.Long", "int64");
        javaToProtoTypeMap.put("long", "int64");
        javaToProtoTypeMap.put("java.lang.Float", "float");
        javaToProtoTypeMap.put("float", "float");
        javaToProtoTypeMap.put("java.lang.Double", "double");
        javaToProtoTypeMap.put("double", "double");
        javaToProtoTypeMap.put("java.util.List", "repeated");
        javaToProtoTypeMap.put("java.util.Set", "repeated");
        javaToProtoTypeMap.put("java.util.Collection", "repeated");
        javaToProtoTypeMap.put("java.util.Map", "map");
        javaToProtoTypeMap.put("java.time.LocalDate", "string");
        javaToProtoTypeMap.put("java.time.LocalDateTime", "string");
        javaToProtoTypeMap.put("java.time.ZonedDateTime", "string");
        javaToProtoTypeMap.put("java.util.Date", "string");
        javaToProtoTypeMap.put("java.math.BigDecimal", "string");
        javaToProtoTypeMap.put("java.math.BigInteger", "string");
        javaToProtoTypeMap.put("byte[]", "bytes");
    }

    public static SettingsState getInstance() {
        return ApplicationManager.getApplication().getService(SettingsState.class);
    }

    @Nullable
    @Override
    public SettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public Map<String, String> getJavaToProtoTypeMap() {
        return javaToProtoTypeMap;
    }

    public boolean isGenerateSeparateFilesForNestedClasses() {
        return generateSeparateFilesForNestedClasses;
    }

    public void setGenerateSeparateFilesForNestedClasses(boolean generateSeparateFilesForNestedClasses) {
        this.generateSeparateFilesForNestedClasses = generateSeparateFilesForNestedClasses;
    }

    public String getSpecifiedProtoPackagePath() {
        return specifiedProtoPackagePath;
    }

    public void setSpecifiedProtoPackagePath(String specifiedProtoPackagePath) {
        this.specifiedProtoPackagePath = specifiedProtoPackagePath;
    }

    public String getProtoSyntax() {
        return protoSyntax;
    }

    public void setProtoSyntax(String protoSyntax) {
        this.protoSyntax = protoSyntax;
    }
}
