package ru.stech.ttn4j.number.utils.interfaces;

import ru.stech.ttn4j.number.utils.RussianNumberParserOptions;
import ru.stech.ttn4j.number.utils.RussianNumberParserResult;

import java.util.List;

/**
 * Parser for russian numbers
 */
public interface RussianNumberParser {

    /**
     * Parsing with mask
     * @param text input that need to parse
     * @param mask the regular expression like (89.........)
     * @return
     */
    List<RussianNumberParserResult> parseWithMask(String text, String mask);

    /**
     * Parsing
     * @param text input that need to parse
     * @return
     */
    RussianNumberParserResult parse(String text);

    /**
     * Parsing with options
     * @param text input that need to parse
     * @param options Options for parsing
     * @return
     */
    RussianNumberParserResult parse(String text, RussianNumberParserOptions options);

    String getStringValue(long value);
}
