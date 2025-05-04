package com.amriteshgupta.pojotoproto.integration;

import com.amriteshgupta.pojotoproto.parser.POJO2ProtoParser;
import com.amriteshgupta.pojotoproto.parser.POJO2ProtoParserFactory;
import com.amriteshgupta.pojotoproto.settings.SettingsState;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UastContextKt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Integration tests for POJO to Proto conversion
 */
public class POJO2ProtoIntegrationTest extends BasePlatformTestCase {

    private POJO2ProtoParser parser;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        parser = POJO2ProtoParserFactory.createParser();
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    /**
     * Test conversion of a simple POJO using test data files
     */
    public void testSimpleUserConversion() throws IOException {
        // Load test file
        PsiFile psiFile = myFixture.configureByFile("pojo/SimpleUser.java");
        assertTrue("File should be a Java file", psiFile instanceof PsiJavaFile);
        
        PsiClass psiClass = ((PsiJavaFile) psiFile).getClasses()[0];
        UClass uClass = UastContextKt.toUElement(psiClass, UClass.class);
        
        // Convert to proto
        Map<String, StringBuilder> result = parser.uElementToProtoString(uClass);
        
        // Verify result
        assertNotNull("Result should not be null", result);
        assertFalse("Result should not be empty", result.isEmpty());
        
        String protoContent = result.get("com.example.SimpleUser").toString();
        assertNotNull("Proto content should not be null", protoContent);
        
        // Compare with expected output
        String expectedContent = FileUtil.loadFile(
                new File(getTestDataPath() + "/expected/SimpleUser.proto"), 
                StandardCharsets.UTF_8);
        
        // Normalize line endings
        expectedContent = expectedContent.replace("\r\n", "\n");
        protoContent = protoContent.replace("\r\n", "\n");
        
        assertEquals("Generated proto should match expected output", 
                expectedContent, protoContent);
    }
    
    /**
     * Test conversion with proto2 syntax
     */
    public void testProto2Conversion() throws IOException {
        // Set proto syntax to proto2
        SettingsState settings = SettingsState.getInstance();
        String originalSyntax = settings.getProtoSyntax();
        settings.setProtoSyntax("proto2");
        
        try {
            // Load test file
            PsiFile psiFile = myFixture.configureByFile("pojo/SimpleUser.java");
            assertTrue("File should be a Java file", psiFile instanceof PsiJavaFile);
            
            PsiClass psiClass = ((PsiJavaFile) psiFile).getClasses()[0];
            UClass uClass = UastContextKt.toUElement(psiClass, UClass.class);
            
            // Convert to proto
            Map<String, StringBuilder> result = parser.uElementToProtoString(uClass);
            
            // Verify result
            assertNotNull("Result should not be null", result);
            assertFalse("Result should not be empty", result.isEmpty());
            
            String protoContent = result.get("com.example.SimpleUser").toString();
            assertNotNull("Proto content should not be null", protoContent);
            
            // Compare with expected output
            String expectedContent = FileUtil.loadFile(
                    new File(getTestDataPath() + "/expected/SimpleUser_proto2.proto"), 
                    StandardCharsets.UTF_8);
            
            // Normalize line endings
            expectedContent = expectedContent.replace("\r\n", "\n");
            protoContent = protoContent.replace("\r\n", "\n");
            
            assertEquals("Generated proto should match expected output", 
                    expectedContent, protoContent);
        } finally {
            // Restore original syntax
            settings.setProtoSyntax(originalSyntax);
        }
    }
}
