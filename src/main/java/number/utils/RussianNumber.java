package number.utils;


import lombok.var;
import number.utils.beans.RussianNumberTokens;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RussianNumber {

    private static int MAX_TOKEN_LENGTH = 13;

    public static RussianNumberParserResult Parse(String text) {
        return Parse(text, new RussianNumberParserOptions());
    }

    public static List<NumericToken> NumericTokens(String text, RussianNumberParserOptions options) {
        // разбиваем текст на токены
        var stringTokens = text.split("\\s+"); // TODO _rgSplitter.Split(text);
        var tokens = new ArrayList<NumericToken>(stringTokens.length);
        var D = new RefContainer<double[][]>(new double[2][MAX_TOKEN_LENGTH]);

        // обрабатываем токены
        for (var item : stringTokens) {
            tokens.addAll(ParseTokens(item, options, D, 0));
        }
        return tokens;
    }

    public static RussianNumberParserResult Parse(String text, RussianNumberParserOptions options) {
        if (options == null) {
            options = new RussianNumberParserOptions();
        }

        if (text.isEmpty()) return RussianNumberParserResult.GetFailed();
        text = text.trim().toLowerCase();


        // вспомогательные переменные
        Long globalLevel = null, localLevel = null;
        Long globalValue = null, localValue = null;
        boolean wasCriticalError = false;

        var tokens = NumericTokens(text, options);
        // цикл по токенам
        int n = tokens.size();
        for (int i = 0; i < n; i++) {
                var token = tokens.get(i);
            if (token.getError() > options.getMaxTokenError()) continue;

            var tokenValue = token.getValue();
            var value = tokenValue.getValue();
            var level = tokenValue.getLevel();
            var multiplier = tokenValue.isMultiplier();
            if(level == -1){
                var buf = localValue;
                localValue = 0l;
                for(var t = 0; t< buf; t++){
                   localValue = Long.valueOf(String.format("%s%s", (localValue != null ? localValue.toString() : ""), value));

                }
            } else
            if (multiplier) {
                // множитель

                if (globalLevel == null || globalLevel > level) {
                    globalValue = (globalValue != null ? globalValue : 0)
                            + (localValue != null ? localValue : 1) * value;

                    globalLevel = level;
                    localValue = null;
                    localLevel = null;

                    token.setSignificant(true);
                } else {
                    // ошибка несоответствия уровней
                    token.setError(1);
                    token.setSignificant(true);
                    wasCriticalError = true;
                }
            } else {
                // простое числительное
                if (localLevel == null || localLevel > level) {
                    localValue = (localValue != null ? localValue : 0) + value;
                    localLevel = level;

                    token.setSignificant(true);
                } else if (localLevel == level) {
                    localValue = Long.valueOf(String.format("%s%s", (localValue != null ? localValue.toString() : ""), value));
                    localLevel = level;
                    token.setSignificant(true);
                } else {
                    // ошибка несоответствия уровней
                    token.setError(1);
                    token.setSignificant(true);
                    wasCriticalError = true;
                }
            }
        }

        // считаем общий уровень ошибки
        NumericToken[] doubleStreamErrors;
        doubleStreamErrors = tokens.stream().filter(e -> e.isSignificant()).toArray(NumericToken[]::new);
        Arrays.stream(doubleStreamErrors).forEach(x -> {
            if (x.getError() == 0.0d) x.setError(1d);
        });

        double totalError = Arrays.stream(doubleStreamErrors).mapToDouble(e -> e.getError()).average().getAsDouble();
        if (wasCriticalError) {
            // имело место критическая ошибка
            if (totalError >= 0.5) totalError = 1;
            else totalError *= 2;
        }

        return new RussianNumberParserResult((globalValue != null ? globalValue : 0) + (localValue != null ? localValue : 0), totalError);
    }

    /// <summary>
    /// распознать токены
    /// </summary>
    /// <param name="str"> строковое представление </param>
    /// <param name="options"> настройки </param>
    /// <param name="D"> матрица </param>
    /// <param name="level"> уровень рекурсии </param>
    /// <returns></returns>
    private static List<NumericToken> ParseTokens(String str, RussianNumberParserOptions options, RefContainer<double[][]> D, int level) {

        var numeral = TOKENS.getOrDefault(str, null);
        if (numeral != null) {
            Numeral finalNumeral3 = numeral;
            return new ArrayList<NumericToken>() {{
                add(new NumericToken(finalNumeral3));
            }};
        }

        int length = str.length();
        if (length < 2) {
            // слишком короткая строка
            return EMPTY_TOKEN_ARRAY;
        } else {
            // строка не найдена => просчитываем варианты
            boolean complexParsing = length >= 6 && level <= 2;
            var variants = complexParsing ? new ArrayList<ArrayList<NumericToken>>() : null;

            // вспомогательные переменные
            double minimalError, error;

            ////////////////////////////////////////////////////////////////
            // односложная фраза
            ////////////////////////////////////////////////////////////////

            if (length <= MAX_TOKEN_LENGTH) {
                // пытаемся распознать с помощью расстояния Левенштейна
                minimalError = Double.POSITIVE_INFINITY;

               for (var item : TOKENS.entrySet()
                       .stream().sorted(Comparator.comparingLong(x -> x.getValue().getValue())
               ).toArray()) {

                    var t = (Map.Entry<String, Numeral>) item;
                    error = NumeralLevenshtein.CompareStrings(str, t.getKey(), D, true);
                //    System.out.println(String.format("parsed %s, %e", item, error));
                    if (error < minimalError) {

                        numeral = t.getValue();
                //        System.out.println(String.format("\nChanged %s %s", item, numeral.getValue()));
                        minimalError = error;
                    }
                }

                if (minimalError <= options.getMaxTokenError()) {
                    if (complexParsing) {
                        // могут быть другие варианты
                        Numeral finalNumeral = numeral;
                        double finalMinimalError1 = minimalError;
                        variants.add(new ArrayList<NumericToken>() {{
                            add(new NumericToken(finalNumeral, finalMinimalError1));
                        }});
                    } else {
                        Numeral finalNumeral1 = numeral;
                        double finalMinimalError = minimalError;
                        return new ArrayList<NumericToken>() {{
                            add(new NumericToken(finalNumeral1, finalMinimalError));
                        }};
                    }
                } else if (!complexParsing) {
                    if (level == 0) {
                        // на первом уровне игнорируем плохие токены
                        return EMPTY_TOKEN_ARRAY;
                    } else {
                        // в рекурсии возвращаем плохие токены, чтобы они влияли на принятие решения
                        Numeral finalNumeral2 = numeral;
                        double finalMinimalError2 = minimalError;
                        return new ArrayList<NumericToken>() {{
                            add(new NumericToken(finalNumeral2, finalMinimalError2));
                        }};
                    }
                }
            }

            ////////////////////////////////////////////////////////////////
            // составная фраза
            ////////////////////////////////////////////////////////////////

            // строки длиной меньше шести смысла делить нет
            if (complexParsing) {
                for (int i = 3; i <= length - 3; i++) {
                    var left = ParseTokens(str.substring(0, i), options, D, level + 1);
                    var right = ParseTokens(str.substring(i), options, D, level + 1);

                    var union = new LinkedHashSet<NumericToken>() {{
                        addAll(left);
                        addAll(right);

                    }};
                    if (union.size() > 0) {
                        if (union.stream().map(e -> e.getError()).reduce(0.0, Double::sum) != 0)  //.Sum(e = > e.GetError()) !=0)
                        {
                            // ухудшаем общий результат на некую величину
                            union.stream().forEach(e -> e.setError(e.getError() + options.getSplitErrorValue() / union.size()));
                        }

                        // объединяем результат
                        variants.add(new ArrayList<NumericToken>(union));
                    }
                }
            }

            ////////////////////////////////////////////////////////////////
            // выбираем лучший вариант
            ////////////////////////////////////////////////////////////////

            if (variants.size() == 0) {
                return EMPTY_TOKEN_ARRAY;
            } else {
                ArrayList<NumericToken> best = null;

                minimalError = Double.POSITIVE_INFINITY;
                for (var item : variants) {
                    error = item.stream().map(e -> e.getError()).reduce(0.0, Double::sum);
                    if (error < minimalError) {
                        best = item;
                        minimalError = error;
                    }
                }

                return best != null ? best : EMPTY_TOKEN_ARRAY;
            }
        }
    }

    /// <summary>
    /// регулярка для деления строки на токены
    /// </summary>
   // private static final RegEx _rgSplitter = new RegEx("\\s+");

    /// <summary>
    /// максимальная длина токена
    /// </summary>


    /// <summary>
    /// пустой массив токенов
    /// </summary>
    private static final List<NumericToken> EMPTY_TOKEN_ARRAY = new ArrayList<NumericToken>();

    /// <summary>
    /// хеш токенов
    /// </summary>
    private static Map<String, Numeral> TOKENS = RussianNumberTokens.getTokens();


    /// <summary>
    /// наименования односложных числительных (мужской род)
    /// </summary>
    private static final String[]_simple_units_male =

    {
        "", "один ", "два ", "три ", "четыре ", "пять ", "шесть ",
                "семь ", "восемь ", "девять ", "десять ", "одиннадцать ",
                "двенадцать ", "тринадцать ", "четырнадцать ", "пятнадцать ",
                "шестнадцать ", "семнадцать ", "восемнадцать ", "девятнадцать "
    }

    ;

    /// <summary>
    /// наименования односложных числительных (женский род)
    /// </summary>
    private static final String[]
    _simple_units_female =

    {
        "", "одна ", "две ", "три ", "четыре ", "пять ", "шесть ",
                "семь ", "восемь ", "девять ", "десять ", "одиннадцать ",
                "двенадцать ", "тринадцать ", "четырнадцать ", "пятнадцать ",
                "шестнадцать ", "семнадцать ", "восемнадцать ", "девятнадцать "
    }

    ;

    /// <summary>
    /// наименования десятков
    /// </summary>
    private static final String[]
    _ten_units =

    {
        "", "десять ", "двадцать ", "тридцать ", "сорок ", "пятьдесят ",
                "шестьдесят ", "семьдесят ", "восемьдесят ", "девяносто "
    }

    ;

    /// <summary>
    /// наименования сотен
    /// </summary>
    private static final String[]
    _hundred_units =

    {
        "", "сто ", "двести ", "триста ", "четыреста ",
                "пятьсот ", "шестьсот ", "семьсот ", "восемьсот ", "девятьсот "
    }

    ;

    /// <summary>
    /// выбрать правильное падежное окончание существительного
    /// </summary>
    /// <param name="value"> число </param>
    /// <param name="form1"> форма существительного в единственном числе </param>
    /// <param name="form2"> форма существительного от двух до четырёх </param>
    /// <param name="form3"> форма существительного от пяти и больше </param>
    /// <returns> возвращает существительное с падежным окончанием, которое соответсвует числу </returns>
    private static String WordForm(long value, String form1, String form2, String form3) {
        switch ((int) ((value % 100 > 20) ? value % 10 : value % 20)) {
            case 1:
                return form1;
            case 2:
            case 3:
            case 4:
                return form2;
            default:
                return form3;
        }
    }

    /// <summary>
    /// перевести в строку числа с учётом падежного окончания относящегося к числу существительного
    /// </summary>
    /// <param name="value"> число </param>
    /// <param name="male"> род существительного, которое относится к числу </param>
    /// <param name="form1"> форма существительного в единственном числе </param>
    /// <param name="form2"> форма существительного от двух до четырёх </param>
    /// <param name="form3"> форма существительного от пяти и больше </param>
    /// <returns></returns>
    private static String ToString(long value, boolean male, String form1, String form2, String form3) {
        var n = value % 1000;

        if (n == 0) return "";
        else if (n < 0) throw new IllegalArgumentException("Параметр не может быть отрицательным");

        // выбираем единицы в зависимости от рода
        var units = male ? _simple_units_male : _simple_units_female;

        var sb = new StringBuilder(_hundred_units[(int) (n / 100)]);

        var rem100 = n % 100;
        if (rem100 < 20) {
            sb.append(units[(int) rem100]);
        } else {
            sb.append(_ten_units[(int) (rem100 / 10)]);
            sb.append(units[(int) (n % 10)]);
        }

        sb.append(WordForm(n, form1, form2, form3));

        if (sb.length() != 0) sb.append(" ");
        return sb.toString();
    }

    /// <summary>
    /// перевести целое число в строку
    /// </summary>
    /// <param name="value"> число </param>
    /// <param name="capitalize"> указывает, что необходимо сделать первую букву заглавной </param>
    /// <returns> возвращает строковую запись числа </returns>

    public static String ToString(long value) {
       return ToString(value, true);
    }

    public static String ToString(long value, boolean capitalize) {
        boolean minus = value < 0;
        if (minus) value = -value;

        var n = value;

        var sb = new StringBuilder();

        if (n == 0) sb.append("ноль ");
        if (n % 1000 != 0) {
            sb.append(ToString(n, true, "", "", ""));
        }

        n /= 1000;

        sb.insert(0, ToString(n, false, "тысяча", "тысячи", "тысяч"));
        n /= 1000;

        sb.insert(0, ToString(n, true, "миллион", "миллиона", "миллионов"));
        n /= 1000;

        sb.insert(0, ToString(n, true, "миллиард", "миллиарда", "миллиардов"));
        n /= 1000;

        sb.insert(0, ToString(n, true, "триллион", "триллиона", "триллионов"));
        n /= 1000;

        sb.insert(0, ToString(n, true, "квадриллион", "квадриллиона", "квадриллионов"));
        if (minus) sb.insert(0, "минус ");

        // делаем первую букву заглавной
        if (capitalize) sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));

        return sb.toString().trim();
    }
}
