package ru.stech.ttn4j.number.utils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RussianNumberParserResult {

    /**
     * значение
     */
    private long value;

    /**
     * ошибка распознования
     */
    private double error;


    /**
     * ошибочный результат
     */
    private static RussianNumberParserResult failed = new RussianNumberParserResult(0, 1);

    /**
     * @param value значение
     */
    public RussianNumberParserResult(long value) {
        this(value, 0);
    }

    /**
     * @param value значение
     * @param error ошибка
     */
    public RussianNumberParserResult(long value, double error) {
        this.value = value;
        this.error = error;
    }


    @Override
    public String toString() {
        return String.valueOf(value);
    }


    public static RussianNumberParserResult getFailed() {
        return failed;
    }

}
