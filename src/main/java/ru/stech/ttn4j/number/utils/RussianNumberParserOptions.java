package ru.stech.ttn4j.number.utils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RussianNumberParserOptions {

    /**
     * настройки по умолчанию
     */
    private static RussianNumberParserOptions defaultOptions;
    private double maxTokenError = 0.67;
    private double splitErrorValue = 0.1;

    /**
     * настройки по умолчанию
     */
    public static RussianNumberParserOptions getDefault() {
        if (defaultOptions == null) defaultOptions = new RussianNumberParserOptions();
        return defaultOptions;
    }
}
