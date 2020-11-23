package number.utils;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class NumeralLevenshtein {

    private NumeralLevenshtein() {
        throw new IllegalStateException("Utility class");
    }

    /// <summary>
    /// таблица стоимости вставки
    /// </summary>
    private static Map<Character, Double> insert;

    /// <summary>
    /// таблица стоимости удаления
    /// </summary>
    private static Map<Character, Double> delete;

    /// <summary>
    /// таблица стоимости замены
    /// </summary>
    private static Map<String, Double> update;

    /// <summary>
    /// стоимость вставки нетабличного символа
    /// </summary>
    private static double insertNonTableChar;

    /// <summary>
    /// стоимость удаления нетабличного символа
    /// </summary>
    private static double deleteNonTableChar;

    /// <summary>
    /// стоимость замены нетабличного символа
    /// </summary>
    private static double updateNonTableChar;

    public static double compareStrings(String s1, String s2) {
        return compareStrings(s1, s2, true);
    }

    /// <summary>
    /// определить степень похожести строк (расстояние Левенштейна)
    /// </summary>
    /// <param name="s1"> строка 1 </param>
    /// <param name="s2"> строка 2 </param>
    /// <param name="relative"> указывает, что необходимо считать относительную похожесть </param>
    /// <returns></returns>
    public static double compareStrings(String s1, String s2, boolean relative) {
        int m = s1.length();
        int n = s2.length();
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        int b = 0;
        int a = 1;
        double[][] matrix = new double[2][n + 1];
        for (var i = 0; i <= m; i++) {
            for (var j = 0; j <= n; j++) {
                if (i != 0 || j != 0) {
                    double costDelete;
                    double costInsert;
                    if (i == 0) {
                        // считаем стоимость вставки
                        costInsert = insert.getOrDefault(s2.charAt(j - 1), insertNonTableChar);
                        matrix[1][j] = matrix[1][j - 1] + costInsert;
                    } else if (j == 0) {
                        // считаем стоимость удаления
                        costDelete = delete.getOrDefault(s1.charAt(i - 1), deleteNonTableChar);
                        matrix[a][0] = matrix[b][0] + costDelete;
                    } else {
                        char c1 = s1.charAt(i - 1);
                        char c2 = s2.charAt(j - 1);
                        if (c1 != c2) {
                            // считаем стоимость удаления
                            costDelete = delete.getOrDefault(c1, deleteNonTableChar);
                            costInsert = insert.getOrDefault(c2, insertNonTableChar);
                            String key = String.format("%s%s", c1, c2);
                            var costUpdate = update.getOrDefault(key,
                                    updateNonTableChar);

                            matrix[a][j] = Math.min(Math.min(
                                    matrix[b][j] + costDelete,
                                    matrix[a][j - 1] + costInsert),
                                    matrix[b][j - 1] + costUpdate
                            );
                        } else {
                            matrix[a][j] = matrix[b][j - 1];
                        }
                    }
                }
            }
            int buf = a;
            a = b;
            b = buf;
        }
        return relative ? matrix[b][n] / n : matrix[b][n];
    }

    private static ArrayList<String[]> readFromInputStream(InputStream inputStream)
            throws IOException {
        ArrayList<String[]> data = new ArrayList<>(64);
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\r", "").replace("\n", "");
                var splited  = line.split("\t");
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

        ArrayList<String[]> data = readLevenshteinData();
        ////////////////////////////////////////////////////////////////
        // формируем таблицы
        ////////////////////////////////////////////////////////////////

        int n = data.get(0).length - 1;
        insert = new LinkedHashMap<>(n);
        delete = new LinkedHashMap<>(n);
        update = new LinkedHashMap<>(n * (n - 1));

        // читаем список символов

        int row = 0;
        Character[] chars = Arrays.stream(data.get(row++)).skip(1).map(e -> e.charAt(0)).toArray(Character[]::new);
        double minCost = Double.POSITIVE_INFINITY;
        double maxCost = 0;

        // INSERT
        var insertCostArray = Arrays.stream(data.get(row++)).skip(1).toArray(String[]::new);
        for (var index = 0; index < insertCostArray.length; index++) {
            var value = parse(insertCostArray[index]);
            insert.put(chars[index], value);
            if (value < minCost) minCost = value;
            if (value > maxCost) maxCost = value;
        }

        // DELETE
        var deleteCostArray = Arrays.stream(data.get(row++)).skip(1).toArray(String[]::new);
        for (var index = 0; index < deleteCostArray.length; index++) {
            try {
                var value = parse(deleteCostArray[index]);
                delete.put(chars[index], value);
                if (value < minCost) minCost = value;
                if (value > maxCost) maxCost = value;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        // UPDATE
        var updateCostArray = Arrays.stream(data.get(row)).skip(1).toArray(String[]::new);
        for (int i = 0; i < n; i++) {

            for (var index = 0; index < updateCostArray.length; index++) {
                if (i != index) {
                    var value = parse(updateCostArray[index]);
                    update.put(chars[i].toString() + chars[index].toString(), value);
                    if (value < minCost) minCost = value;
                    if (value > maxCost) maxCost = value;
                }
            }
        }
        // прочие показатели
        insertNonTableChar = maxCost;
        deleteNonTableChar = minCost;
        updateNonTableChar = minCost;
    }

    private static ArrayList<String[]> readLevenshteinData() {
        ArrayList<String[]> data = new ArrayList<>();
        Class<NumeralLevenshtein> nLevClass = NumeralLevenshtein.class;
        try (InputStream inputStream = nLevClass.getResourceAsStream("/NumeralLevenshteinData.txt")) {
            data = readFromInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /// <summary>
    /// распарсить значение из файла данных
    /// </summary>
    /// <param name="str"> строка </param>
    /// <param name="default"> значение по умолчанию </param>
    /// <returns></returns>
    private static double parse(String str) {
        return parse(str, 1);
    }

    private static double parse(String str, double defaultValue) {
        if (str.isEmpty() || " ".equals(str)) {
            return defaultValue;
        } else {
            return Double.parseDouble(str);
        }
    }

}
