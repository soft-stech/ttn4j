package number.utils;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

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

    /**
     * таблица стоимости вставки
     */
    private static Map<Character, Double> insert;

    /**
     * таблица стоимости удаления
     */
    private static Map<Character, Double> delete;

    /**
     * таблица стоимости замены
     */
    private static Map<String, Double> update;

    /**
     * стоимость вставки нетабличного символа
     */
    private static double insertNonTableChar;

    /**
     * стоимость удаления нетабличного символа
     */
    private static double deleteNonTableChar;

    /**
     * стоимость замены нетабличного символа
     */
    private static double updateNonTableChar;

    public static double compareStrings(String s1, String s2) {
        return compareStrings(s1, s2, true);
    }

    /**
     * определить степень похожести строк (расстояние Левенштейна)
     *
     * @param firstString  строка 1
     * @param secondString строка 2
     * @param relative     указывает, что необходимо считать относительную похожесть
     * @return Значение расстояние Левенштейна для данных строк
     */
    public static double compareStrings(String firstString, String secondString, boolean relative) {
        int firsStringLength = firstString.length();
        int secondStringLength = secondString.length();
        firstString = firstString.toLowerCase();
        secondString = secondString.toLowerCase();
        var b = 0;
        var a = 1;
        var matrix = new double[2][secondStringLength + 1];
        for (var firsStringIndex = 0; firsStringIndex <= firsStringLength; firsStringIndex++) {
            for (var secondStringIndex = 0; secondStringIndex <= secondStringLength; secondStringIndex++) {
                if (firsStringIndex != 0 || secondStringIndex != 0) {
                    double costDelete;
                    double costInsert;
                    if (firsStringIndex == 0) {
                        // считаем стоимость вставки
                        costInsert = insert.getOrDefault(secondString.charAt(secondStringIndex - 1), insertNonTableChar);
                        matrix[1][secondStringIndex] = matrix[1][secondStringIndex - 1] + costInsert;
                    } else if (secondStringIndex == 0) {
                        // считаем стоимость удаления
                        costDelete = delete.getOrDefault(firstString.charAt(firsStringIndex - 1), deleteNonTableChar);
                        matrix[a][0] = matrix[b][0] + costDelete;
                    } else {
                        char firstChar = firstString.charAt(firsStringIndex - 1);
                        char secondChar = secondString.charAt(secondStringIndex - 1);
                        calculateDeletionCost(matrix[b], a, matrix, secondStringIndex, firstChar, secondChar);
                        if (firstChar != secondChar) {
                            // считаем стоимость удаления
                            calculateDeletionCost(matrix[b], a, matrix, secondStringIndex, firstChar, secondChar);
                        } else {
                            matrix[a][secondStringIndex] = matrix[b][secondStringIndex - 1];
                        }
                    }
                }
            }
            int buf = a;
            a = b;
            b = buf;
        }
        return relative ? matrix[b][secondStringLength] / secondStringLength : matrix[b][secondStringLength];
    }

    private static void calculateDeletionCost(double[] matrix1, int a, double[][] matrix, int j, char c1, char c2) {
        var costDelete = delete.getOrDefault(c1, deleteNonTableChar);
        var costInsert = insert.getOrDefault(c2, insertNonTableChar);
        String key = String.format("%s%s", c1, c2);
        var costUpdate = update.getOrDefault(key,
                updateNonTableChar);

        matrix[a][j] = minFromThree(
                matrix1[j] + costDelete,
                matrix[a][j - 1] + costInsert,
                matrix1[j - 1] + costUpdate
        );
    }

    private static double minFromThree(double first, double second, double third) {
        return Math.min(Math.min(first, second), third);
    }

    private static ArrayList<String[]> readFromInputStream(InputStream inputStream)
            throws IOException {
        ArrayList<String[]> data = new ArrayList<>(64);
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\r", "").replace("\n", "");
                var splited = line.split("\t");
                data.add(splited);

            }
        }
        return data;
    }

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
        var chars = Arrays.stream(data.get(row++)).skip(1).map(e -> e.charAt(0)).toArray(Character[]::new);
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
            var value = parse(deleteCostArray[index]);
            delete.put(chars[index], value);
            if (value < minCost) minCost = value;
            if (value > maxCost) maxCost = value;
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

    /**
     * Чтение файла
     *
     * @return
     */
    @SneakyThrows
    private static ArrayList<String[]> readLevenshteinData() {
        var data = new ArrayList<String[]>();
        var nLevClass = NumeralLevenshtein.class;
        try (var inputStream = nLevClass.getResourceAsStream("/NumeralLevenshteinData.txt")) {
            data = readFromInputStream(inputStream);
        }
        return data;
    }

    /**
     * распарсить значение из файла данных
     *
     * @param str строка
     */
    private static double parse(String str) {
        return parse(str, 1);
    }


    /**
     * распарсить значение из файла данных
     *
     * @param str          строка
     * @param defaultValue значение по умолчанию
     */
    private static double parse(String str, double defaultValue) {
        if (StringUtils.isNotBlank(str)) {
            return Double.parseDouble(str);
        } else {
            return defaultValue;
        }
    }

}
