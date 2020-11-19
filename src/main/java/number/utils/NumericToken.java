package number.utils;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NumericToken {

    /// <summary>
    /// значение
    /// </summary>
    private Numeral value;

    /// <summary>
    /// ошибка распознавания
    /// </summary>
    private double error;

    /// <summary>
    /// указывает, что токен был использован для распознавания
    /// </summary>
    private boolean isSignificant;

    public NumericToken(Numeral value){
        this(value, 0);
    }

    public NumericToken(Numeral value, double error){
       this.value = value;
       this.error = error;
    }
}
