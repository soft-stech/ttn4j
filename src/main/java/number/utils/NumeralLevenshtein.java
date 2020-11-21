package number.utils;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.var;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Data
@Accessors(chain = true)
public class NumeralLevenshtein {
    /// <summary>
    /// таблица стоимости вставки
    /// </summary>
    private static Map<Character, Double> _insert;

    /// <summary>
    /// таблица стоимости удаления
    /// </summary>
    private static Map<Character, Double> _delete;

    /// <summary>
    /// таблица стоимости замены
    /// </summary>
    private static Map<String, Double> _update;

    /// <summary>
    /// стоимость вставки нетабличного символа
    /// </summary>
    private static double _insertNonTableChar;

    /// <summary>
    /// стоимость удаления нетабличного символа
    /// </summary>
    private static double _deleteNonTableChar;

    /// <summary>
    /// стоимость замены нетабличного символа
    /// </summary>
    private static double _updateNonTableChar;

    public static double CompareStrings(String s1, String s2) {
        return CompareStrings(s1, s2, true);
    }

    /// <summary>
    /// определить степень похожести строк (расстояние Левенштейна)
    /// </summary>
    /// <param name="s1"> строка 1 </param>
    /// <param name="s2"> строка 2 </param>
    /// <param name="relative"> указывает, что необходимо считать относительную похожесть </param>
    /// <returns></returns>
    public static double CompareStrings(String s1, String s2, boolean relative) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int m = s1.length(), n = s2.length();

        // вспомогательные переменные
        int i, j, a = 1, b = 0;
        char c1, c2;
        double[][] D = new double[2][n + 1];
        double costInsert, costDelete, costUpdate;

        for (i = 0; i <= m; i++) {
            for (j = 0; j <= n; j++) {
                if (i != 0 || j != 0) {
                    if (i == 0) {
                        // считаем стоимость вставки
                        costInsert = _insert.getOrDefault(s2.charAt(j - 1), _insertNonTableChar);
                        D[1][j] = D[1][j - 1] + costInsert;
                    } else if (j == 0) {
                        // считаем стоимость удаления
                        costDelete = _delete.getOrDefault(s1.charAt(i - 1), _deleteNonTableChar);
                        D[a][0] = D[b][0] + costDelete;
                    } else {
                        c1 = s1.charAt(i - 1);
                        c2 = s2.charAt(j - 1);
                        if (c1 != c2) {
                            // считаем стоимость удаления
                            costDelete = _delete.getOrDefault(c1, _deleteNonTableChar);
                            costInsert = _insert.getOrDefault(c2, _insertNonTableChar);
                            costUpdate = _update.getOrDefault(new CharCartage(c1, c2), _updateNonTableChar);
                            D[a][j] = Math.min(Math.min(
                                    D[b][j] + costDelete,
                                    D[a][j - 1] + costInsert),
                                    D[b][j - 1] + costUpdate
                            );
                        } else {
                            D[a][j] = D[b][j - 1];
                        }
                    }
                }
            }
            var buf = a;
            a = b;
            b = a;
        }

        return relative ? D[b][n] / n : D[b][n];
    }

    public static double CompareStrings(String s1, String s2, RefContainer<double[][]> D) {
        return CompareStrings(s1, s2, D, true);
    }

    /// <summary>
    /// определить степень похожести строк (расстояние Левенштейна)
    /// </summary>
    /// <param name="s1"> строка 1 </param>
    /// <param name="s2"> строка 2 </param>
    /// <param name="D"> матрица </param>
    /// <param name="relative"> указывает, что необходимо считать относительную похожесть </param>
    /// <returns></returns>
    public static double CompareStrings(String s1, String s2, RefContainer<double[][]> D, boolean relative) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int m = s1.length(), n = s2.length();

        // вспомогательные переменные
        int i, j, a = 1, b = 0;
        char c1, c2;
        if (D.getValue()[0].length < 2 || D.getValue()[1].length < n + 1) D.setValue(new double[2][n + 1]);
        else {
            // очищаем массив
            for (j = 0; j <= n; j++) {
                D.getValue()[0][j] = 0;
                D.getValue()[1][j] = 0;
            }
        }
        double costInsert, costDelete, costUpdate;

        for (i = 0; i <= m; i++) {
            for (j = 0; j <= n; j++) {
                if (i != 0 || j != 0) {
                    if (i == 0) {
                        // считаем стоимость вставки
                        costInsert = _insert.getOrDefault(s2.charAt(j - 1), _insertNonTableChar);
                        D.getValue()[1][j] = D.getValue()[1][j - 1] + costInsert;
                    } else if (j == 0) {
                        // считаем стоимость удаления
                        costDelete = _delete.getOrDefault(s1.charAt(i - 1), _deleteNonTableChar);
                        D.getValue()[a][0] = D.getValue()[b][0] + costDelete;
                    } else {
                        c1 = s1.charAt(i - 1);
                        c2 = s2.charAt(j - 1);
                        if (c1 != c2) {
                            // считаем стоимость удаления
                            costDelete = _delete.getOrDefault(c1, _deleteNonTableChar);
                            costInsert = _insert.getOrDefault(c2, _insertNonTableChar);
                            var key = String.format("%s%s", c1, c2);
                            costUpdate = _update.getOrDefault(key,
                                    _updateNonTableChar);

                            D.getValue()[a][j] = Math.min(Math.min(
                                    D.getValue()[b][j] + costDelete,
                                    D.getValue()[a][j - 1] + costInsert),
                                    D.getValue()[b][j - 1] + costUpdate
                            );
                        } else {
                            D.getValue()[a][j] = D.getValue()[b][j - 1];
                        }
                    }
                }
            }
            var buf = a;
            a = b;
            b = buf;
        }

        return relative ? D.getValue()[b][n] / n :
                D.getValue()[b][n];
    }

    private static ArrayList<String[]> readFromInputStream(InputStream inputStream)
            throws IOException {
        var data = new ArrayList<String[]>(64);
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                var trimed =  line.replace("\r", "").replace("\n", "");
                var splited = trimed.split("\t");
                data.add(splited);

            }
        }
        return data;
    }

    /// <summary>
    /// статический конструктор
    /// </summary>
    static {
        ////////////////////////////////////////////////////////////////
        // загружаем данные о символах
        ////////////////////////////////////////////////////////////////

        ArrayList<String[]> data = new ArrayList<>();
        Class nLevClass = NumeralLevenshtein.class;
        try (InputStream inputStream = nLevClass.getResourceAsStream("/NumeralLevenshteinData.txt")) {
            data = readFromInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ////////////////////////////////////////////////////////////////
        // формируем таблицы
        ////////////////////////////////////////////////////////////////

        int n = data.get(0).length - 1, row = 0;
        double minCost = Double.POSITIVE_INFINITY, maxCost = 0;

        _insert = new LinkedHashMap<Character, Double>(n);
        _delete = new LinkedHashMap<Character, Double>(n);
        _update = new LinkedHashMap<String, Double>(n * (n - 1));

        // читаем список символов

        Character[] chars = Arrays.stream(data.get(row++)).skip(1).map(e -> e.charAt(0)).toArray(Character[]::new);

        // INSERT

        var newArr1 = Arrays.stream(data.get(row)).skip(1).toArray(String[]::new);
        for (var index = 0; index < newArr1.length; index++) {
            var value = Parse(newArr1[index]);
            _insert.put(chars[index], value);
            if (value < minCost) minCost = value;
            if (value > maxCost) maxCost = value;
        }

        // DELETE
        row++;
        var newArr2 = Arrays.stream(data.get(row)).skip(1).toArray(String[]::new);
        for (var index = 0; index < newArr2.length; index++) {
            try {
                var value = Parse(newArr2[index]);
                _delete.put(chars[index], value);
                if (value < minCost) minCost = value;
                if (value > maxCost) maxCost = value;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        row++;
        // UPDATE

        var newArr3 = Arrays.stream(data.get(row)).skip(1).toArray(String[]::new);
        for (int i = 0; i < n; i++) {

            for (var index = 0; index < newArr3.length; index++) {
                if (i != index) {
                    var value = Parse(newArr3[index]);
                    _update.put(chars[i].toString() + chars[index].toString(), value);
                    if (value < minCost) minCost = value;
                    if (value > maxCost) maxCost = value;
                }
            }
        }
        row++;
        // прочие показатели
        _insertNonTableChar = maxCost;
        _deleteNonTableChar = minCost;
        _updateNonTableChar = minCost;
    }

    /// <summary>
    /// распарсить значение из файла данных
    /// </summary>
    /// <param name="str"> строка </param>
    /// <param name="default"> значение по умолчанию </param>
    /// <returns></returns>
    private static double Parse(String str) {
        return Parse(str, 1);
    }

    private static double Parse(String str, double defaultValue) {
        if (str.isEmpty() || " ".equals(str)) {
            return defaultValue;
        } else {
            return Double.parseDouble(str);
        }
    }

}
