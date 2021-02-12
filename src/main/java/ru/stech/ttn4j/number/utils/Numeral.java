package ru.stech.ttn4j.number.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Numeral {

    /**
     * Значение
     */
    private String value;

    /**
     * Уровень
     */
    private long level;

    /**
     * Признак множителя - тысяча, миллион и прочие числительные, умножаемые на коэффициент перед ними
     */
    private boolean isMultiplier;
}
