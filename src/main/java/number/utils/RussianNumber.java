package number.utils;

import number.utils.beans.RussianNumberTokens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class RussianNumber {

    private RussianNumber() {
        throw new IllegalStateException("Utility class");
    }

    private static final int MAX_TOKEN_LENGTH = 13;

    public static RussianNumberParserResult parse(String text) {
        return parse(text, new RussianNumberParserOptions());
    }

    public static List<NumericToken> numericTokens(String text, RussianNumberParserOptions options) {
        // разбиваем текст на токены
        var stringTokens = text.split("\\s+");
        var tokens = new ArrayList<NumericToken>(stringTokens.length);
        // обрабатываем токены
        for (var item : stringTokens) {
            tokens.addAll(parseTokens(item, options, 0));
        }
        return tokens;
    }

    public static RussianNumberParserResult parse(String text, RussianNumberParserOptions options) {
        if (options == null) {
            options = new RussianNumberParserOptions();
        }

        if (text.isEmpty()) {
            return RussianNumberParserResult.getFailed();
        }
        text = text.trim().toLowerCase();


        // вспомогательные переменные
        Long globalLevel = null;
        Long localLevel = null;
        Long globalValue = null;
        Long localValue = null;
        boolean wasCriticalError = false;

        var tokens = numericTokens(text, options);
        for (NumericToken token : tokens) {
            if (token.getError() > options.getMaxTokenError()) {
                continue;
            }
            var tokenValue = token.getNumeral();
            var value = tokenValue.getValue();
            var level = tokenValue.getLevel();
            var multiplier = tokenValue.isMultiplier();
            if (level == -1) {
                token.setSignificant(true);
                var buf = localValue;
                localValue = 0L;
                for (var t = 0; t < buf; t++) {
                    localValue = Long.valueOf(String.format("%s%s", localValue.toString(), value));
                }
            } else if (multiplier) {
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
                    localValue = Long.valueOf(String.format("%s%s", localValue.toString(), value));
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
        doubleStreamErrors = tokens.stream().filter(NumericToken::isSignificant).toArray(NumericToken[]::new);
        Arrays.stream(doubleStreamErrors).forEach(x -> {
            if (x.getError() == 0.0d) x.setError(1d);
        });

        double totalError = Arrays.stream(doubleStreamErrors).mapToDouble(NumericToken::getError).average().getAsDouble();
        if (wasCriticalError) {
            // имело место критическая ошибка
            if (totalError >= 0.5) totalError = 1;
            else totalError *= 2;
        }

        return new RussianNumberParserResult((globalValue != null ? globalValue : 0) + (localValue != null ? localValue : 0), totalError);
    }


    /**
     * Распознать токены
     *
     * @param str     строковое представление
     * @param options настройки
     * @param level   уровень рекурсии
     * @return
     */
    private static List<NumericToken> parseTokens(String str, RussianNumberParserOptions options, int level) {

        var numeral = tokens.getOrDefault(str, null);
        if (numeral != null) {
            return Collections.singletonList(new NumericToken(numeral));
        }

        var length = str.length();
        if (length < 2) {
            // слишком короткая строка
            return EMPTY_TOKEN_ARRAY;
        } else {
            // строка не найдена => просчитываем варианты
            var complexParsing = length >= 6 && level <= 2;
            var variants = complexParsing ? new ArrayList<ArrayList<NumericToken>>() : null;

            // вспомогательные переменные
            double minimalError;
            double error;
            ////////////////////////////////////////////////////////////////
            // односложная фраза
            ////////////////////////////////////////////////////////////////

            if (length <= MAX_TOKEN_LENGTH) {
                // пытаемся распознать с помощью расстояния Левенштейна
                minimalError = Double.POSITIVE_INFINITY;
                for (var item : tokens.entrySet()) {
                    error = NumeralLevenshtein.compareStrings(str, item.getKey(), true);
                    if (error < minimalError) {
                        numeral = item.getValue();
                        minimalError = error;
                    }
                }
                if (minimalError <= options.getMaxTokenError()) {
                    if (complexParsing) {
                        variants.add((ArrayList<NumericToken>) getNumericTokens(numeral, minimalError));
                    } else {
                        return getNumericTokens(numeral, minimalError);
                    }
                } else if (!complexParsing) {
                    if (level == 0) {
                        // на первом уровне игнорируем плохие токены
                        return EMPTY_TOKEN_ARRAY;
                    } else {
                        // в рекурсии возвращаем плохие токены, чтобы они влияли на принятие решения
                        return getNumericTokens(numeral, minimalError);
                    }
                }
            }

            ////////////////////////////////////////////////////////////////
            // составная фраза
            ////////////////////////////////////////////////////////////////

            // строки длиной меньше шести смысла делить нет
            if (complexParsing) {
                devideInputedText(str, options, level, length, variants);
            }
            return getBestVariant(variants);
        }
    }

    private static List<NumericToken> getNumericTokens(Numeral numeral, double minimalError) {
        ArrayList<NumericToken> numericTokens = new ArrayList<>();
        numericTokens.add(new NumericToken(numeral, minimalError));
        return numericTokens;
    }

    private static List<NumericToken> getBestVariant(ArrayList<ArrayList<NumericToken>> variants) {
        ////////////////////////////////////////////////////////////////
        // выбираем лучший вариант
        ////////////////////////////////////////////////////////////////
        double minimalError;
        double error;
        if (variants == null || variants.isEmpty()) {
            return EMPTY_TOKEN_ARRAY;
        } else {
            ArrayList<NumericToken> best = null;

            minimalError = Double.POSITIVE_INFINITY;
            for (var item : variants) {
                error = item.stream().map(NumericToken::getError).reduce(0.0, Double::sum);
                if (error < minimalError) {
                    best = item;
                    minimalError = error;
                }
            }

            return best != null ? best : EMPTY_TOKEN_ARRAY;
        }
    }

    private static void devideInputedText(String str, RussianNumberParserOptions options, int level, int length, ArrayList<ArrayList<NumericToken>> variants) {
        for (int i = 3; i <= length - 3; i++) {
            var left = parseTokens(str.substring(0, i), options, level + 1);
            var right = parseTokens(str.substring(i), options, level + 1);

            var union = new LinkedHashSet<NumericToken>();
            union.addAll(left);
            union.addAll(right);
            if (!union.isEmpty()) {
                if (union.stream().map(NumericToken::getError).reduce(0.0, Double::sum) != 0)  //.Sum(e = > e.GetError()) !=0)
                {
                    // ухудшаем общий результат на некую величину
                    union.forEach(e -> e.setError(e.getError() + options.getSplitErrorValue() / union.size()));
                }

                // объединяем результат
                variants.add(new ArrayList<>(union));
            }
        }
    }

    /*
    / <summary>
    / пустой массив токенов
    / </summary>
    */
    private static final List<NumericToken> EMPTY_TOKEN_ARRAY = new ArrayList<>();

    /*
    / <summary>
    / хеш токенов
    / </summary>
    */
    private static final Map<String, Numeral> tokens = RussianNumberTokens.getTokens();


    /*
    / <summary>
    / наименования односложных числительных (мужской род)
    / </summary>
    */
    private static final String[] _simple_units_male =

            {
                    "", "один ", "два ", "три ", "четыре ", "пять ", "шесть ",
                    "семь ", "восемь ", "девять ", "десять ", "одиннадцать ",
                    "двенадцать ", "тринадцать ", "четырнадцать ", "пятнадцать ",
                    "шестнадцать ", "семнадцать ", "восемнадцать ", "девятнадцать "
            };

    /*
     / <summary>
     / наименования односложных числительных (женский род)
     / </summary>
    */
    private static final String[]
            _simple_units_female =

            {
                    "", "одна ", "две ", "три ", "четыре ", "пять ", "шесть ",
                    "семь ", "восемь ", "девять ", "десять ", "одиннадцать ",
                    "двенадцать ", "тринадцать ", "четырнадцать ", "пятнадцать ",
                    "шестнадцать ", "семнадцать ", "восемнадцать ", "девятнадцать "
            };

    /*
    / <summary>
    / наименования десятков
    / </summary>
    */
    private static final String[]
            _ten_units =

            {
                    "", "десять ", "двадцать ", "тридцать ", "сорок ", "пятьдесят ",
                    "шестьдесят ", "семьдесят ", "восемьдесят ", "девяносто "
            };

    /**
     * / <summary>
     * / наименования сотен
     * / </summary>
     */
    private static final String[]
            _hundred_units =

            {
                    "", "сто ", "двести ", "триста ", "четыреста ",
                    "пятьсот ", "шестьсот ", "семьсот ", "восемьсот ", "девятьсот "
            };


    /**
     * выбрать правильное падежное окончание существительного
     *
     * @param value число
     * @param form1 форма существительного в единственном числе
     * @param form2 форма существительного от двух до четырёх
     * @param form3 форма существительного от пяти и больше
     * @return возвращает существительное с падежным окончанием, которое соответсвует числу
     */
    private static String wordForm(long value, String form1, String form2, String form3) {
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

    /**
     * перевести в строку числа с учётом падежного окончания относящегося к числу существительного
     *
     * @param value число
     * @param male  род существительного, которое относится к числу
     * @param form1 форма существительного в единственном числе
     * @param form2 форма существительного от двух до четырёх
     * @param form3 форма существительного от пяти и больше
     */
    private static String getStringValue(long value, boolean male, String form1, String form2, String form3) {
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

        sb.append(wordForm(n, form1, form2, form3));

        if (sb.length() != 0) sb.append(" ");
        return sb.toString();
    }


    /**
     * перевести целое число в строку
     *
     * @param value число
     * @return
     */
    public static String getStringValue(long value) {
        return getStringValue(value, true);
    }

    /**
     * перевести целое число в строку
     *
     * @param value      число
     * @param capitalize указывает, что необходимо сделать первую букву заглавной
     * @return возвращает строковую запись числа
     */
    public static String getStringValue(long value, boolean capitalize) {
        boolean minus = value < 0;
        if (minus) value = -value;

        var n = value;

        var sb = new StringBuilder();

        if (n == 0) sb.append("ноль ");
        if (n % 1000 != 0) {
            sb.append(getStringValue(n, true, "", "", ""));
        }

        n /= 1000;

        sb.insert(0, getStringValue(n, false, "тысяча", "тысячи", "тысяч"));
        n /= 1000;

        sb.insert(0, getStringValue(n, true, "миллион", "миллиона", "миллионов"));
        n /= 1000;

        sb.insert(0, getStringValue(n, true, "миллиард", "миллиарда", "миллиардов"));
        n /= 1000;

        sb.insert(0, getStringValue(n, true, "триллион", "триллиона", "триллионов"));
        n /= 1000;

        sb.insert(0, getStringValue(n, true, "квадриллион", "квадриллиона", "квадриллионов"));
        if (minus) sb.insert(0, "минус ");

        // делаем первую букву заглавной
        if (capitalize) sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));

        return sb.toString().trim();
    }
}
