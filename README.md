# ttn4j
Simple text-to-numbers library wriiten in java. Based on https://github.com/Doomer3D/Genesis.CV
 
# examples
##
 You can tune the tokenMaxError option to get optimal results
 ```JAVA
   var parsed =
                RussianNumber.parse("ваше число",  new RussianNumberParserOptions().setMaxTokenError(0.3));
 ```
 maxTokenError = 0.3 is optimal if you have a some trash input like a "Думаю девать", with this value library skip "Думаю" and trying to parse "девять"
 you can set any value from 0.0 to 1.0 and find best value for your task
## text to digit
  To convert from text to digit value try to run this code
 ```JAVA
        import number.utils.RussianNumber;
        // some code
        string text = "сто двадцать три"
        var parsed = RussianNumber.parse(value.Text);
 ```

## digit to text
  To convert from number to text value try to run this example
```JAVA
        import number.utils.RussianNumber;
        // some code
        long value = 123;
        var parsed = RussianNumber.getStringValue(value);
```

