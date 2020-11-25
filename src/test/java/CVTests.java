import number.utils.RussianNumber;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

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

    private final static int SIZE = 66;

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
        var position = 63;
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
    void CycleRun(){
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

    static {
        Class testsClass = CVTests.class;
        try (InputStream inputStream = testsClass.getResourceAsStream("/TestData.txt")) {
            testData = readFromInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

