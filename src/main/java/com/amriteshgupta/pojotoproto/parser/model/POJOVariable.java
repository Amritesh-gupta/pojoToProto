package com.amriteshgupta.pojotoproto.parser.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents a variable in a Java class for Protocol Buffer conversion
 */
public class POJOVariable {
    private final PsiVariable psiVariable;
    private final PsiType psiType;
    private final Map<String, PsiType> psiClassGenerics;
    private final int recursionLevel;

    public POJOVariable(PsiVariable psiVariable, PsiType psiType) {
        this(psiVariable, psiType, Map.of(), 0);
    }

    public POJOVariable(PsiVariable psiVariable, PsiType psiType, Map<String, PsiType> psiClassGenerics) {
        this(psiVariable, psiType, psiClassGenerics, 0);
    }

    public POJOVariable(PsiVariable psiVariable, PsiType psiType, Map<String, PsiType> psiClassGenerics, int recursionLevel) {
        this.psiVariable = psiVariable;
        this.psiType = psiType;
        this.psiClassGenerics = psiClassGenerics;
        this.recursionLevel = recursionLevel;
    }

    public static POJOVariable init(@NotNull PsiVariable psiVariable, Map<String, PsiType> psiClassGenerics) {
        return new POJOVariable(psiVariable, psiVariable.getType(), psiClassGenerics);
    }

    public PsiType getPsiType() {
        return psiType;
    }

    public int getRecursionLevel() {
        return recursionLevel;
    }

    public String getName() {
        return psiVariable.getName();
    }
}
