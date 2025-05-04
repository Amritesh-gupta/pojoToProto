package com.amriteshgupta.pojotoproto.settings;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.Map;

/**
 * Tests for the SettingsState class
 */
public class SettingsStateTest extends BasePlatformTestCase {

    /**
     * Test default settings initialization
     */
    public void testDefaultSettings() {
        SettingsState settings = SettingsState.getInstance();
        
        // Check default values
        assertEquals("Default proto syntax should be proto3", "proto3", settings.getProtoSyntax());
        assertFalse("Generate separate files should be false by default", 
                settings.isGenerateSeparateFilesForNestedClasses());
        assertEquals("Specified proto package path should be empty by default", 
                "", settings.getSpecifiedProtoPackagePath());
        
        // Check type mappings
        Map<String, String> typeMap = settings.getJavaToProtoTypeMap();
        assertNotNull("Type map should not be null", typeMap);
        assertFalse("Type map should not be empty", typeMap.isEmpty());
        
        // Check some specific mappings
        assertEquals("String should map to string", "string", typeMap.get("java.lang.String"));
        assertEquals("Integer should map to int32", "int32", typeMap.get("java.lang.Integer"));
        assertEquals("int should map to int32", "int32", typeMap.get("int"));
        assertEquals("boolean should map to bool", "bool", typeMap.get("boolean"));
        assertEquals("List should map to repeated", "repeated", typeMap.get("java.util.List"));
        assertEquals("Map should map to map", "map", typeMap.get("java.util.Map"));
    }
    
    /**
     * Test settings modification
     */
    public void testSettingsModification() {
        SettingsState settings = SettingsState.getInstance();
        
        // Save original values
        String originalSyntax = settings.getProtoSyntax();
        boolean originalGenerateSeparate = settings.isGenerateSeparateFilesForNestedClasses();
        String originalPath = settings.getSpecifiedProtoPackagePath();
        
        try {
            // Modify settings
            settings.setProtoSyntax("proto2");
            settings.setGenerateSeparateFilesForNestedClasses(true);
            settings.setSpecifiedProtoPackagePath("/test/path");
            
            // Verify changes
            assertEquals("Proto syntax should be updated", "proto2", settings.getProtoSyntax());
            assertTrue("Generate separate files should be updated", 
                    settings.isGenerateSeparateFilesForNestedClasses());
            assertEquals("Specified proto package path should be updated", 
                    "/test/path", settings.getSpecifiedProtoPackagePath());
        } finally {
            // Restore original values
            settings.setProtoSyntax(originalSyntax);
            settings.setGenerateSeparateFilesForNestedClasses(originalGenerateSeparate);
            settings.setSpecifiedProtoPackagePath(originalPath);
        }
    }
}
