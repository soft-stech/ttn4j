package number.utils;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NumericToken {

    /// <summary>
    /// значение
    /// </summary>
    private Numeral numeral;

    /// <summary>
    /// ошибка распознавания
    /// </summary>
    private double error;

    /// <summary>
    /// указывает, что токен был использован для распознавания
    /// </summary>
    private boolean isSignificant;

    public NumericToken(Numeral numeral){
        this(numeral, 0);
    }

    public NumericToken(Numeral numeral, double error){
       this.numeral = numeral;
       this.error = error;
    }
}
