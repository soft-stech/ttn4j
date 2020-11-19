

import lombok.var;
import number.utils.RussianNumber;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class CVTests{
    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");


    @Test
    public void TestTest(){
        var bytes = "пятьсот девять".getBytes();
        var str = new String(bytes, StandardCharsets.UTF_8);
        var parsed = RussianNumber.Parse(str);
        Assert.assertEquals(509, parsed.getValue());
    }
}

