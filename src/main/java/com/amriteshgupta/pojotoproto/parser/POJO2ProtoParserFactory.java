package com.amriteshgupta.pojotoproto.parser;

/**
 * Factory for creating POJO2ProtoParser instances
 */
public class POJO2ProtoParserFactory {

    /**
     * Create a new POJO2ProtoParser instance
     *
     * @return POJO2ProtoParser instance
     */
    public static POJO2ProtoParser createParser() {
        return new POJO2ProtoParser();
    }
}
