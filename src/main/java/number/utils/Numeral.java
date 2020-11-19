package number.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class Numeral {
    /// <summary>
    /// значение
    /// </summary>
    private long value;

    /// <summary>
    /// уровень
    /// </summary>
    private long level;

    /// <summary>
    /// признак множителя
    /// </summary>
    /// <remarks>
    /// тысяча, миллион и прочие числительные, умножаемые на коэффициент перед ними
    /// </remarks>
    private boolean isMultiplier;

}
