package number.utils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RussianNumberParserResult {

    /// <summary>
    /// значение
    /// </summary>
    private long value;

    /// <summary>
    /// ошибка распознавания
    /// </summary>
    private double error;

    /// <summary>
    /// ошибочный результат
    /// </summary>
    private static RussianNumberParserResult failed = new RussianNumberParserResult(0, 1);


    /// <summary>
    /// конструктор
    /// </summary>
    /// <param name="value"> значение </param>
    public RussianNumberParserResult(long value) {
        this(value, 0);
    }

    /// <summary>
    /// конструктор
    /// </summary>
    /// <param name="value"> значение </param>
    /// <param name="error"> ошибка распознавания </param>
    public RussianNumberParserResult(long value, double error) {
        this.value = value;
        this.error = 0;
    }


    @Override
    public String toString() {
        return String.valueOf(value);
    }



    public static RussianNumberParserResult GetFailed() {
        return failed;
    }

}
