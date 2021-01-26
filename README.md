# ttn4j
Simple text-to-numbers library wriiten in java. Based on https://github.com/Doomer3D/Genesis.CV
 
# examples
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
