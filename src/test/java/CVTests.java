

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.var;
import number.utils.RussianNumber;

import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Data
@AllArgsConstructor
class TestData {
    public long Value;
    public String Text;
}

class CVTests {
    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private static ArrayList<TestData> testData = null;

    private final static int SIZE = 63;

    @RepeatedTest(value = SIZE, name = "SimpleTest {currentRepetition}/{totalRepetitions}")
        //size of testData
    void repeatedTests(RepetitionInfo repetitionInfo) {

        var value = testData.get(repetitionInfo.getCurrentRepetition() - 1);
        System.out.println("=====================");
        System.out.println(String.format("test[%d] - %d:%s", repetitionInfo.getCurrentRepetition(),value.Value,value.Text) );
        var parsed = RussianNumber.parse(value.Text);
        assertEquals(value.Value, parsed.getValue(), value.Text);
        System.out.println("passed");
    }

    @Test
    void numberTest() {

        var value = testData.get(53);
        System.out.println("=====================");
        System.out.println(String.format("%d:%s",value.Value,value.Text) );
        var parsed = RussianNumber.parse(value.Text);
        assertEquals(value.Value, parsed.getValue(), value.Text);
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

