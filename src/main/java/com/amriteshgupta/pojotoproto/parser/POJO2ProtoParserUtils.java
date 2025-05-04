package com.amriteshgupta.pojotoproto.parser;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility methods for POJO2ProtoParser
 */
public class POJO2ProtoParserUtils {

    /**
     * Convert PSI text to string
     *
     * @param text PSI text
     * @return String
     */
    public static String psiTextToString(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        return text.replaceAll("\"", "");
    }

    /**
     * Convert array text to list
     *
     * @param text Array text
     * @return List of strings
     */
    public static List<String> arrayTextToList(String text) {
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .map(s -> s.replaceAll("\"", "").replaceAll("\\{", "").replaceAll("\\}", ""))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    /**
     * Convert doc text to list
     *
     * @param tagName Tag name
     * @param text    Doc text
     * @return List of strings
     */
    public static List<String> docTextToList(String tagName, String text) {
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }

        Pattern pattern = Pattern.compile(tagName + "\\s+(.+)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String content = matcher.group(1);
            return Arrays.stream(content.split(","))
                    .map(String::trim)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
