package com.amriteshgupta.pojotoproto.parser.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a field in a Java class for Protocol Buffer conversion
 */
public class POJOField extends POJOVariable {
    private final POJOClass pojoClass;
    private final PsiField psiField;
    private String name;

    private POJOField(PsiField psiField, POJOClass pojoClass) {
        super(psiField, psiField.getType());
        this.psiField = psiField;
        this.pojoClass = pojoClass;
    }

    public static POJOField init(@NotNull PsiField psiField, @NotNull POJOClass pojoClass) {
        return new POJOField(psiField, pojoClass);
    }

    public PsiField getPsiField() {
        return psiField;
    }

    public POJOClass getPojoClass() {return pojoClass;}

    public String getName() {
        return name != null ? name : psiField.getName();
    }

    public String getCamelCaseName() {
        String fieldName = psiField.getName();
        if (fieldName == null || fieldName.isEmpty()) {
            return "";
        }
        return fieldName;
    }

    @Override
    public String toString() {
        return "POJOField{" +
                "name='" + getName() + '\'' +
                ", type=" + getPsiType().getCanonicalText() +
                '}';
    }
}
