package number.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class Numeral {

    /**
     * Значение
     */
    private long value;

    /**
     * Уровень
     */
    private long level;

    /**
     * Признак множителя - тысяча, миллион и прочие числительные, умножаемые на коэффициент перед ними
     */
    private boolean isMultiplier;

}
