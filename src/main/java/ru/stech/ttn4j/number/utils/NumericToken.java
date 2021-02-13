package ru.stech.ttn4j.number.utils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NumericToken {

    /**
     * значение
     */

    private Numeral numeral;

    /**
     * ошибка распознавания
     */
    private double error;

    /**
     * указывает, что токен был использован для распознавания
     */
    private boolean isSignificant;

    public NumericToken(Numeral numeral) {
        this(numeral, 0);
    }

    public NumericToken(Numeral numeral, double error) {
        this.numeral = numeral;
        this.error = error;
    }
}
