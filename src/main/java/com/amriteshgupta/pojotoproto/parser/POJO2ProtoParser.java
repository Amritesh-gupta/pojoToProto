package com.amriteshgupta.pojotoproto.parser;

import com.amriteshgupta.pojotoproto.parser.model.enums.CollectionType;
import com.amriteshgupta.pojotoproto.parser.model.POJOClass;
import com.amriteshgupta.pojotoproto.parser.model.POJOField;
import com.amriteshgupta.pojotoproto.parser.model.enums.SourceRoot;
import com.amriteshgupta.pojotoproto.settings.SettingsState;
import com.intellij.psi.*;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UVariable;

import java.io.File;
import java.util.*;

/**
 * Main parser class for converting Java POJOs to Protocol Buffers
 */
public class POJO2ProtoParser {

    private final SettingsState settings;
    private final Map<String, String> javaToProtoTypeMap;
    private final List<String> iterableTypes = List.of(CollectionType.getCollectionTypes().stream()
            .map(Class::getCanonicalName)
            .toArray(String[]::new));
    private final Set<String> processedClasses = new HashSet<>();
    private final Map<String, StringBuilder> messageBuilders = new HashMap<>();
    private final Map<String, Set<String>> linkedClassesToParentClass = new HashMap<>();

    public POJO2ProtoParser() {
        this.settings = SettingsState.getInstance();
        this.javaToProtoTypeMap = settings.getJavaToProtoTypeMap();
    }


    public Map<String, StringBuilder> uElementToProtoString(@NotNull final UElement uElement) {
        Map<String, StringBuilder> messageToPackageName = new HashMap<>();

        if (uElement instanceof UVariable variable) {
            PsiType type = variable.getType();
            PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(type);
            if (psiClass != null) {
                return processPsiClass(psiClass);
            }
        } else if (uElement instanceof UClass) {
            PsiClass psiClass = ((UClass) uElement).getJavaPsi();
            return processPsiClass(psiClass);
        }
        
        return messageToPackageName;
    }

    private Map<String, StringBuilder> processPsiClass(PsiClass psiClass) {
        Map<String, StringBuilder> messageToPackageName = new HashMap<>();
        StringBuilder result = new StringBuilder();
        POJOClass classToProcess = POJOClass.init(psiClass);
        String parentPackageName = classToProcess.getPsiClass().getQualifiedName();
        processClass(classToProcess);

        if (!settings.isGenerateSeparateFilesForNestedClasses()) {
            result.append("syntax = \"").append(settings.getProtoSyntax()).append("\";\n\n");
            for (StringBuilder messageBuilder : messageBuilders.values()) {
                result.append(messageBuilder).append("\n");
            }
            messageToPackageName.put(parentPackageName, result);
        } else {
            for (String packageName : messageBuilders.keySet()) {
                StringBuilder message = getMessageToPackageName(packageName, psiClass);
                messageToPackageName.put(packageName, message);
            }
        }
        return messageToPackageName;
    }

    private StringBuilder getMessageToPackageName(String packageName, PsiClass psiClass) {
        StringBuilder result = new StringBuilder();
        result.append("syntax = \"").append(settings.getProtoSyntax()).append("\";\n\n");
        addImportsIfSettingEnabled(result, packageName, psiClass);
        result.append(messageBuilders.get(packageName));
        return result;
    }

    private void addImportsIfSettingEnabled(StringBuilder result, String parentClassName, PsiClass psiClass) {
        Set<String> linkedClasses = linkedClassesToParentClass.get(parentClassName);
        if(linkedClasses == null || linkedClasses.isEmpty()) {
            return;
        }
        for(String packageName : linkedClasses) {
            String className = packageName.substring(packageName.lastIndexOf('.') + 1);
            result.append("import ").append("\"").append(getPackagePathIfSpecified(psiClass))
                    .append(className).append(".proto\"").append(";\n\n");
        }
    }

    private String getPackagePathIfSpecified(PsiClass psiClass) {
        if(!settings.getSpecifiedProtoPackagePath().isEmpty()) {
            String specifiedProtoPackagePath = settings.getSpecifiedProtoPackagePath();
            specifiedProtoPackagePath = specifiedProtoPackagePath.replace(File.separatorChar, '/');
            String basePath = psiClass.getProject().getBasePath();
            specifiedProtoPackagePath = specifiedProtoPackagePath.substring(basePath.length() + 1);
            for (SourceRoot prefix : SourceRoot.values()) {
                if (specifiedProtoPackagePath.startsWith(prefix.getPath())) {
                    specifiedProtoPackagePath = specifiedProtoPackagePath.substring(prefix.getPath().length());
                    break;
                }
            }
            return specifiedProtoPackagePath + "/";
        }
        return "";
    }


    private void processClass(POJOClass pojoClass) {
        PsiClass psiClass = pojoClass.getPsiClass();
        String packageName = psiClass.getQualifiedName();
        String className = psiClass.getName();
        
        // Skip if already processed
        if (processedClasses.contains(packageName)) {
            return;
        }
        
        processedClasses.add(packageName);
        
        // Create message builder
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("message ").append(className).append(" {\n");
        
        // Reset field counter for each message
        int fieldCounter = 1;
        
        // Process fields
        for (PsiField field : psiClass.getAllFields()) {
            // Skip static fields
            if (field.hasModifierProperty(PsiModifier.STATIC)) {
                continue;
            }
            
            POJOField pojoField = pojoClass.toField(field);
            String fieldDefinition = processField(pojoField, fieldCounter);
            
            if (fieldDefinition != null) {
                messageBuilder.append("  ").append(fieldDefinition).append("\n");
                fieldCounter++;
            }
        }
        
        messageBuilder.append("}");
        messageBuilders.put(packageName, messageBuilder);
    }


    private String processField(POJOField pojoField, int fieldCounter) {
        PsiField field = pojoField.getPsiField();
        POJOClass pojoClass = pojoField.getPojoClass();
        String parentClassName = pojoClass.getPsiClass().getQualifiedName();
        PsiType type = field.getType();
        String fieldName = pojoField.getCamelCaseName();
        
        // Handle primitive types
        if (type instanceof PsiPrimitiveType) {
            String protoType = javaToProtoTypeMap.get(type.getCanonicalText());
            if (protoType != null) {
                return formatFieldDefinition(protoType, fieldName, fieldCounter);
            }
        } 
        // Handle arrays
        else if (type instanceof PsiArrayType) {
            PsiType componentType = type.getDeepComponentType();
            String protoType = getProtoType(componentType, parentClassName, fieldCounter);
            return "repeated " + protoType + " " + fieldName + " = " + fieldCounter + ";";
        } 
        // Handle reference types
        else {
            PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(type);
            if (psiClass != null) {
                String qualifiedName = psiClass.getQualifiedName();
                
                // Handle enums
                if (psiClass.isEnum()) {
                    return processEnum(psiClass, fieldName, parentClassName, fieldCounter);
                }
                
                // Handle collections
                boolean isCollection = false;
                for (String iterableType : iterableTypes) {
                    if (InheritanceUtil.isInheritor(type, iterableType)) {
                        isCollection = true;
                        break;
                    }
                }
                
                if (isCollection) {
                    PsiType itemType = PsiUtil.extractIterableTypeParameter(type, false);
                    if (itemType != null) {
                        String protoType = getProtoType(itemType, parentClassName, fieldCounter);
                        return "repeated " + protoType + " " + fieldName + " = " + fieldCounter + ";";
                    }
                    return "repeated string " + fieldName + " = " + fieldCounter + ";";
                }
                
                // Handle maps
                if (InheritanceUtil.isInheritor(type, Map.class.getCanonicalName())) {
                    return processMap(type, fieldName, parentClassName, fieldCounter);
                }
                
                // Handle known types
                String protoType = javaToProtoTypeMap.get(qualifiedName);
                if (protoType != null) {
                    return formatFieldDefinition(protoType, fieldName, fieldCounter);
                }
                
                // Handle custom classes
                linkedClassesToParentClass.computeIfAbsent(parentClassName, k -> new HashSet<>()).add(qualifiedName);
                if (!processedClasses.contains(psiClass.getQualifiedName())) {
                    processClass(POJOClass.init(psiClass));
                }
                
                return formatFieldDefinition(psiClass.getName(), fieldName, fieldCounter);
            }
        }
        
        // Default to string if type not recognized
        return formatFieldDefinition("string", fieldName, fieldCounter);
    }


    private String processEnum(PsiClass psiClass, String fieldName, String parentClassName, int fieldCounter) {
        String enumName = psiClass.getName();
        String enumQualifiedName = psiClass.getQualifiedName();
        
        // Skip if already processed
        linkedClassesToParentClass.computeIfAbsent(parentClassName, k -> new HashSet<>()).add(enumQualifiedName);
        if (!processedClasses.contains(enumQualifiedName)) {
            processedClasses.add(enumQualifiedName);

            StringBuilder enumBuilder = new StringBuilder();
            enumBuilder.append("enum ").append(enumName).append(" {\n");
            
            // Add default UNSPECIFIED value for proto3
            if ("proto3".equals(settings.getProtoSyntax())) {
                enumBuilder.append("  ").append(enumName).append("_UNSPECIFIED = 0;\n");
            }
            
            // Process enum constants
            int enumCounter = 1;
            for (PsiField enumConstant : psiClass.getAllFields()) {
                if (enumConstant instanceof PsiEnumConstant) {
                    String constantName = enumConstant.getName().toUpperCase();
                    enumBuilder.append("  ").append(constantName).append(" = ").append(enumCounter).append(";\n");
                    enumCounter++;
                }
            }
            
            enumBuilder.append("}");
            messageBuilders.put(enumQualifiedName, enumBuilder);
        }
        
        return formatFieldDefinition(enumName, fieldName, fieldCounter);
    }


    private String processMap(PsiType type, String fieldName, String parentClassName, int fieldCounter) {
        PsiType[] parameters = ((PsiClassType) type).getParameters();
        
        if (parameters.length == 2) {
            PsiType keyType = parameters[0];
            PsiType valueType = parameters[1];
            
            String protoKeyType = getProtoType(keyType, parentClassName, fieldCounter);
            String protoValueType = getProtoType(valueType, parentClassName, fieldCounter);
            String comment = "";
            
            // In Protocol Buffers, map keys can only be integral types, strings, or booleans
            if (!protoKeyType.equals("string") && !protoKeyType.equals("int32") && 
                !protoKeyType.equals("int64") && !protoKeyType.equals("bool")) {
                protoKeyType = "string";
            }

            if(!(valueType instanceof PsiPrimitiveType) && PsiUtil.resolveClassInClassTypeOnly(valueType) != null ){
                protoValueType = "string";
                comment = "// map of " + keyType.getPresentableText() + " to " + valueType.getPresentableText() + " is not supported in proto";
            }
            
            return "map<" + protoKeyType + ", " + protoValueType + "> " + fieldName + " = " + fieldCounter + ";" + comment;
        }

        // Default map if parameters not available
        return "map<string, string> " + fieldName + " = " + fieldCounter + ";";
    }


    private String getProtoType(PsiType type, String parentClassName, int fieldCounter) {
        if (type instanceof PsiPrimitiveType) {
            String protoType = javaToProtoTypeMap.get(type.getCanonicalText());
            return protoType != null ? protoType : "string";
        } else {
            PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(type);
            if (psiClass != null) {
                String qualifiedName = psiClass.getQualifiedName();
                
                if (psiClass.isEnum()) {
                    if (!processedClasses.contains(qualifiedName)) {
                        processEnum(psiClass, "", parentClassName, fieldCounter);
                    }
                    return psiClass.getName();
                }
                
                String protoType = javaToProtoTypeMap.get(qualifiedName);
                if (protoType != null) {
                    return protoType;
                }

                linkedClassesToParentClass.computeIfAbsent(parentClassName, k -> new HashSet<>()).add(qualifiedName);
                if (!processedClasses.contains(qualifiedName)) {
                    processClass(POJOClass.init(psiClass));
                }
                
                return psiClass.getName();
            }
        }
        
        return "string";
    }


    private String formatFieldDefinition(String type, String name, int fieldCounter) {
        StringBuilder builder = new StringBuilder();

        if (settings.getProtoSyntax().equals("proto2")) {
            builder.append("optional ");
        }
        
        builder.append(type).append(" ").append(name).append(" = ").append(fieldCounter).append(";");
        
        return builder.toString();
    }
}
