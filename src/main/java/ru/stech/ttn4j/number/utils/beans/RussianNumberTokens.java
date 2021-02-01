package ru.stech.ttn4j.number.utils.beans;

import ru.stech.ttn4j.number.utils.Numeral;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class RussianNumberTokens {

    public static Map<String, Numeral> getTokens() {
        return TOKENS;
    }

    private RussianNumberTokens() {
        throw new IllegalStateException("Utility class");
    }

    private static final Map<String, Numeral> TOKENS = new HashMap<>();

    static {
        TOKENS.put(new String("лит".getBytes(), StandardCharsets.UTF_8), new Numeral(1337, 4, false));
        TOKENS.put(new String("нулей".getBytes(), StandardCharsets.UTF_8), new Numeral(0, -1, false));
        TOKENS.put(new String("еденица".getBytes(), StandardCharsets.UTF_8), new Numeral(1, -1, false));
        TOKENS.put(new String("двоек".getBytes(), StandardCharsets.UTF_8), new Numeral(2, -1, false));
        TOKENS.put(new String("троек".getBytes(), StandardCharsets.UTF_8), new Numeral(3, -1, false));
        TOKENS.put(new String("четверок".getBytes(), StandardCharsets.UTF_8), new Numeral(4, -1, false));
        TOKENS.put(new String("пятерок".getBytes(), StandardCharsets.UTF_8), new Numeral(5, -1, false));
        TOKENS.put(new String("шестерок".getBytes(), StandardCharsets.UTF_8), new Numeral(6, -1, false));
        TOKENS.put(new String("семерок".getBytes(), StandardCharsets.UTF_8), new Numeral(7, -1, false));
        TOKENS.put(new String("восьмерок".getBytes(), StandardCharsets.UTF_8), new Numeral(8, -1, false));
        TOKENS.put(new String("девяток".getBytes(), StandardCharsets.UTF_8), new Numeral(9, -1, false));

        TOKENS.put(new String("ноль".getBytes(), StandardCharsets.UTF_8), new Numeral(0, 1, false));
        TOKENS.put(new String("один".getBytes(), StandardCharsets.UTF_8), new Numeral(1, 1, false));
        TOKENS.put(new String("одна".getBytes(), StandardCharsets.UTF_8), new Numeral(1, 1, false));
        TOKENS.put(new String("два".getBytes(), StandardCharsets.UTF_8), new Numeral(2, 1, false));
        TOKENS.put(new String("две".getBytes(), StandardCharsets.UTF_8), new Numeral(2, 1, false));
        TOKENS.put(new String("три".getBytes(), StandardCharsets.UTF_8), new Numeral(3, 1, false));
        TOKENS.put(new String("четыре".getBytes(), StandardCharsets.UTF_8), new Numeral(4, 1, false));
        TOKENS.put(new String("пять".getBytes(), StandardCharsets.UTF_8), new Numeral(5, 1, false));
        TOKENS.put(new String("шесть".getBytes(), StandardCharsets.UTF_8), new Numeral(6, 1, false));
        TOKENS.put(new String("семь".getBytes(), StandardCharsets.UTF_8), new Numeral(7, 1, false));
        TOKENS.put(new String("восемь".getBytes(), StandardCharsets.UTF_8), new Numeral(8, 1, false));
        TOKENS.put(new String("девять".getBytes(), StandardCharsets.UTF_8), new Numeral(9, 1, false));
        TOKENS.put(new String("десять".getBytes(), StandardCharsets.UTF_8), new Numeral(10, 1, false));
        TOKENS.put(new String("одиннадцать".getBytes(), StandardCharsets.UTF_8), new Numeral(11, 1, false));
        TOKENS.put(new String("двенадцать".getBytes(), StandardCharsets.UTF_8), new Numeral(12, 1, false));
        TOKENS.put(new String("тринадцать".getBytes(), StandardCharsets.UTF_8), new Numeral(13, 1, false));
        TOKENS.put(new String("четырнадцать".getBytes(), StandardCharsets.UTF_8), new Numeral(14, 1, false));
        TOKENS.put(new String("пятнадцать".getBytes(), StandardCharsets.UTF_8), new Numeral(15, 1, false));
        TOKENS.put(new String("шестнадцать".getBytes(), StandardCharsets.UTF_8), new Numeral(16, 1, false));
        TOKENS.put(new String("семнадцать".getBytes(), StandardCharsets.UTF_8), new Numeral(17, 1, false));
        TOKENS.put(new String("восемнадцать".getBytes(), StandardCharsets.UTF_8), new Numeral(18, 1, false));
        TOKENS.put(new String("девятнадцать".getBytes(), StandardCharsets.UTF_8), new Numeral(19, 1, false));
        TOKENS.put(new String("двадцать".getBytes(), StandardCharsets.UTF_8), new Numeral(20, 2, false));
        TOKENS.put(new String("тридцать".getBytes(), StandardCharsets.UTF_8), new Numeral(30, 2, false));
        TOKENS.put(new String("сорок".getBytes(), StandardCharsets.UTF_8), new Numeral(40, 2, false));
        TOKENS.put(new String("пятьдесят".getBytes(), StandardCharsets.UTF_8), new Numeral(50, 2, false));
        TOKENS.put(new String("шестьдесят".getBytes(), StandardCharsets.UTF_8), new Numeral(60, 2, false));
        TOKENS.put(new String("семьдесят".getBytes(), StandardCharsets.UTF_8), new Numeral(70, 2, false));
        TOKENS.put(new String("восемьдесят".getBytes(), StandardCharsets.UTF_8), new Numeral(80, 2, false));
        TOKENS.put(new String("девяносто".getBytes(), StandardCharsets.UTF_8), new Numeral(90, 2, false));
        TOKENS.put(new String("сто".getBytes(), StandardCharsets.UTF_8), new Numeral(100, 3, false));
        TOKENS.put(new String("двести".getBytes(), StandardCharsets.UTF_8), new Numeral(200, 3, false));
        TOKENS.put(new String("триста".getBytes(), StandardCharsets.UTF_8), new Numeral(300, 3, false));
        TOKENS.put(new String("четыреста".getBytes(), StandardCharsets.UTF_8), new Numeral(400, 3, false));
        TOKENS.put(new String("пятьсот".getBytes(), StandardCharsets.UTF_8), new Numeral(500, 3, false));
        TOKENS.put(new String("шестьсот".getBytes(), StandardCharsets.UTF_8), new Numeral(600, 3, false));
        TOKENS.put(new String("семьсот".getBytes(), StandardCharsets.UTF_8), new Numeral(700, 3, false));
        TOKENS.put(new String("восемьсот".getBytes(), StandardCharsets.UTF_8), new Numeral(800, 3, false));
        TOKENS.put(new String("девятьсот".getBytes(), StandardCharsets.UTF_8), new Numeral(900, 3, false));
        TOKENS.put(new String("тысяч".getBytes(), StandardCharsets.UTF_8), new Numeral(1000, 4, true));
        TOKENS.put(new String("тысяча".getBytes(), StandardCharsets.UTF_8), new Numeral(1000, 4, true));
        TOKENS.put(new String("тысячи".getBytes(), StandardCharsets.UTF_8), new Numeral(1000, 4, true));
        TOKENS.put(new String("миллион".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000, 5, true));
        TOKENS.put(new String("миллиона".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000, 5, true));
        TOKENS.put(new String("миллионов".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000, 5, true));
        TOKENS.put(new String("миллиард".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000000, 6, true));
        TOKENS.put(new String("миллиарда".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000000, 6, true));
        TOKENS.put(new String("миллиардов".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000000, 6, true));
        TOKENS.put(new String("триллион".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000000000L, 7, true));
        TOKENS.put(new String("триллиона".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000000000L, 7, true));
        TOKENS.put(new String("триллионов".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000000000L, 7, true));
        TOKENS.put(new String("квадриллион".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000000000000L, 8, true));
        TOKENS.put(new String("квадриллиона".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000000000000L, 8, true));
        TOKENS.put(new String("квадриллионов".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000000000000L, 8, true));
        TOKENS.put(new String("трешка".getBytes(), StandardCharsets.UTF_8), new Numeral(3, 1, true));
        TOKENS.put(new String("трешки".getBytes(), StandardCharsets.UTF_8), new Numeral(3, 1, true));
        TOKENS.put(new String("трешкек".getBytes(), StandardCharsets.UTF_8), new Numeral(3, 1, true));
        TOKENS.put(new String("червонец".getBytes(), StandardCharsets.UTF_8), new Numeral(10, 1, true));
        TOKENS.put(new String("червонца".getBytes(), StandardCharsets.UTF_8), new Numeral(10, 1, true));
        TOKENS.put(new String("червонцев".getBytes(), StandardCharsets.UTF_8), new Numeral(10, 1, true));
        TOKENS.put(new String("дюжина".getBytes(), StandardCharsets.UTF_8), new Numeral(12, 1, true));
        TOKENS.put(new String("дюжины".getBytes(), StandardCharsets.UTF_8), new Numeral(12, 1, true));
        TOKENS.put(new String("дюжин".getBytes(), StandardCharsets.UTF_8), new Numeral(12, 1, true));
        TOKENS.put(new String("полтос".getBytes(), StandardCharsets.UTF_8), new Numeral(50, 1, true));
        TOKENS.put(new String("полтоса".getBytes(), StandardCharsets.UTF_8), new Numeral(50, 1, true));
        TOKENS.put(new String("полтосов".getBytes(), StandardCharsets.UTF_8), new Numeral(50, 1, true));
        TOKENS.put(new String("полтинник".getBytes(), StandardCharsets.UTF_8), new Numeral(50, 1, true));
        TOKENS.put(new String("полтинника".getBytes(), StandardCharsets.UTF_8), new Numeral(50, 1, true));
        TOKENS.put(new String("полтинников".getBytes(), StandardCharsets.UTF_8), new Numeral(50, 1, true));
        TOKENS.put(new String("сотка".getBytes(), StandardCharsets.UTF_8), new Numeral(100, 3, true));
        TOKENS.put(new String("сотки".getBytes(), StandardCharsets.UTF_8), new Numeral(100, 3, true));
        TOKENS.put(new String("соток".getBytes(), StandardCharsets.UTF_8), new Numeral(100, 3, true));
        TOKENS.put(new String("стольник".getBytes(), StandardCharsets.UTF_8), new Numeral(100, 3, true));
        TOKENS.put(new String("стольника".getBytes(), StandardCharsets.UTF_8), new Numeral(100, 3, true));
        TOKENS.put(new String("стольников".getBytes(), StandardCharsets.UTF_8), new Numeral(100, 3, true));
        TOKENS.put(new String("пятихатка".getBytes(), StandardCharsets.UTF_8), new Numeral(500, 3, true));
        TOKENS.put(new String("пятихатки".getBytes(), StandardCharsets.UTF_8), new Numeral(500, 3, true));
        TOKENS.put(new String("пятихаток".getBytes(), StandardCharsets.UTF_8), new Numeral(500, 3, true));
        TOKENS.put(new String("тыща".getBytes(), StandardCharsets.UTF_8), new Numeral(1000, 4, true));
        TOKENS.put(new String("тыщи".getBytes(), StandardCharsets.UTF_8), new Numeral(1000, 4, true));
        TOKENS.put(new String("тыщ".getBytes(), StandardCharsets.UTF_8), new Numeral(1000, 4, true));
        TOKENS.put(new String("штука".getBytes(), StandardCharsets.UTF_8), new Numeral(1000, 4, true));
        TOKENS.put(new String("штуки".getBytes(), StandardCharsets.UTF_8), new Numeral(1000, 4, true));
        TOKENS.put(new String("штук".getBytes(), StandardCharsets.UTF_8), new Numeral(1000, 4, true));
        TOKENS.put(new String("косарь".getBytes(), StandardCharsets.UTF_8), new Numeral(1000, 4, true));
        TOKENS.put(new String("косаря".getBytes(), StandardCharsets.UTF_8), new Numeral(1000, 4, true));
        TOKENS.put(new String("косарей".getBytes(), StandardCharsets.UTF_8), new Numeral(1000, 4, true));
        TOKENS.put(new String("кусок".getBytes(), StandardCharsets.UTF_8), new Numeral(1000, 4, true));
        TOKENS.put(new String("куска".getBytes(), StandardCharsets.UTF_8), new Numeral(1000, 4, true));
        TOKENS.put(new String("кусков".getBytes(), StandardCharsets.UTF_8), new Numeral(1000, 4, true));
        TOKENS.put(new String("лям".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000, 5, true));
        TOKENS.put(new String("ляма".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000, 5, true));
        TOKENS.put(new String("лямов".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000, 5, true));
        TOKENS.put(new String("лимон".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000, 5, true));
        TOKENS.put(new String("лимона".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000, 5, true));
        TOKENS.put(new String("лимонов".getBytes(), StandardCharsets.UTF_8), new Numeral(1000000, 5, true));
    }
}
