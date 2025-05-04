package com.amriteshgupta.pojotoproto.parser.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Java class for Protocol Buffer conversion
 */
public class POJOClass {
    private final PsiClass psiClass;

    private POJOClass(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    public static POJOClass init(@NotNull PsiClass psiClass) {
        return new POJOClass(psiClass);
    }

    public PsiClass getPsiClass() {
        return psiClass;
    }

    public POJOField toField(PsiField field) {
        return POJOField.init(field, this);
    }

}
