package number.utils;

import lombok.Data;

@Data
public class RefContainer<T>
{
    public RefContainer(T value)
    {
        this.value = value;
    }

    private T value;

    public T getValue()
    {
        return value;
    }

    public void setValue(T value)
    {
        this.value = value;
    }
}
