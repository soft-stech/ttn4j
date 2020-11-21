package number.utils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RussianNumberParserOptions {
    /// <summary>
    /// настройки по умолчанию
    /// </summary>
    private static RussianNumberParserOptions defaultOptions;
    private double maxTokenError = 0.67;
    private double splitErrorValue = 0.1;

    /// <summary>
    /// настройки по умолчанию
    /// </summary>
    public static RussianNumberParserOptions getDefault()
    {
        if(defaultOptions == null) defaultOptions = new RussianNumberParserOptions();
        return defaultOptions;
    }
}
