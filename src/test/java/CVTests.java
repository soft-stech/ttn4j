import ru.stech.ttn4j.number.utils.RussianNumber;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import ru.stech.ttn4j.number.utils.interfaces.RussianNumberParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


class CVTests {
    private final Charset UTF8_CHARSET = StandardCharsets.UTF_8;

    private static ArrayList<TestData> testData = null;

    private final static int SIZE = 57;

    static {
        Class testsClass = CVTests.class;
        try (InputStream inputStream = testsClass.getResourceAsStream("/TestData.txt")) {
            testData = readFromInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RussianNumberParser RussianNumber = new RussianNumber();

    @RepeatedTest(value = SIZE, name = "SimpleTest {currentRepetition}/{totalRepetitions}")
    void repeatedTests(RepetitionInfo repetitionInfo) {

        var value = testData.get(repetitionInfo.getCurrentRepetition() - 1);
        System.out.println("=====================");
        System.out.println(String.format("test[%d] - %d:%s", repetitionInfo.getCurrentRepetition(),value.Value,value.Text) );
        var parsed = RussianNumber.parse(value.Text);
        assertEquals(value.Value, parsed.getValue(), value.Text);
        System.out.println("passed");
    }

    @RepeatedTest(value = SIZE, name = "SimpleTest {currentRepetition}/{totalRepetitions}")
    void parseFromNumbersToTextTest(RepetitionInfo repetitionInfo) {

        var value = testData.get(repetitionInfo.getCurrentRepetition() - 1);
        System.out.println("=====================");
        System.out.println(String.format("test[%d] - %d:%s", repetitionInfo.getCurrentRepetition(),value.Value,value.Text) );
        var parsed = RussianNumber.getStringValue(value.Value);
        System.out.println(String.format("number - %s: input - %s: out - %s",value.Value, value.Text, new String(parsed.getBytes(), UTF8_CHARSET)));
        assertEquals(value.Value, RussianNumber.parse(parsed).getValue());
        System.out.println("passed");
    }


    @Test
    void numberTest() {
        var position = 1;
        var value = testData.get(position - 1);
        System.out.println("=====================");
        System.out.println(String.format("%d:%s",value.Value,value.Text) );
        var parsed = RussianNumber.parse(value.Text);
        assertEquals(value.Value, parsed.getValue(), value.Text);
    }

    @Test
    void parseNumbersToTextSingleTest(){
        parseFromNumbersToTextTest(new RepetitionInfo() {
            @Override
            public int getCurrentRepetition() {
                return 54;
            }

            @Override
            public int getTotalRepetitions() {
                return 0;
            }
        });
    }

    @Test
    void cycleRun(){
        for(int i = 0; i < 10000; i++){
            var value = new TestData(i, "");
            System.out.println("=====================");
            System.out.printf("test[%d] - %d:%s%n", i ,value.Value,value.Text);
            var parsed = RussianNumber.getStringValue(value.Value);
            System.out.printf("number - %s: input - %s: out - %s%n",value.Value, value.Text, new String(parsed.getBytes(), UTF8_CHARSET));
            assertEquals(value.Value, RussianNumber.parse(parsed).getValue());
            System.out.println("passed");
        }
    }

    @RepeatedTest(value = 75, name = "MaskTest {currentRepetition}/{totalRepetitions}")
    void repeatedMaskTests(RepetitionInfo repetitionInfo) {
        var testNumber = testData.get(repetitionInfo.getCurrentRepetition() - 1);
        System.out.println("=====================");
        System.out.println(String.format("test[%d] - %d:%s", repetitionInfo.getCurrentRepetition(),testNumber.Value,testNumber.Text) );
        var mask = new String(new char[Integer.valueOf(String.valueOf(testNumber.Value).length())]).replace("\0",".");
        var parsed = RussianNumber.parseWithMask(testNumber.Text, mask);
        assertEquals(parsed.stream().anyMatch(p -> p.getValue() == testNumber.Value), true);
        System.out.println("passed");
    }

    @Test
    void debugMaskMsisdnTest() {
        var position = 76;
        var regular = "********";
        var value = testData.get(position - 1);
        System.out.println("=====================");
        System.out.println(String.format("%d:%s",value.Value,value.Text) );
        var parsed = RussianNumber.parseWithMask(value.Text,regular);
        assertEquals(parsed.get(0).getValue(),value.Value);
    }

    @Test
    void maskMsisdnTest() {
        var position = 72;
        var regular = "89.........";
        var value = testData.get(position - 1);
        System.out.println("=====================");
        System.out.println(String.format("%d:%s",value.Value,value.Text) );
        var parsed = RussianNumber.parseWithMask(value.Text,regular);
        assertEquals(parsed.get(0).getValue(),value.Value);
    }

    @Test
    void maskJumpingLevelTest() {
        var position = 74;
        var value = testData.get(position - 1);
        var regular = "...";
        System.out.println("=====================");
        System.out.println(String.format("%d:%s",value.Value,value.Text) );
        var parsed = RussianNumber.parseWithMask(value.Text,regular);
        assertEquals(parsed.get(0).getValue(),value.Value);
    }

    @Test
    void withoutMaskTest() {
        var position = 75;
        var value = testData.get(position - 1);
        System.out.println("=====================");
        System.out.println(String.format("%d:%s",value.Value,value.Text) );
        var parsed = RussianNumber.parseWithMask(value.Text,null);
        assertEquals(parsed.size(),4);
    }

    private static ArrayList<TestData> readFromInputStream(InputStream inputStream)
            throws IOException {
        ArrayList<TestData> data = new ArrayList<>(64);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                var splited = line.split(":");
                data.add(new TestData(Long.parseLong(splited[0]), splited[1]));
            }
        }
        return data;
    }
}

