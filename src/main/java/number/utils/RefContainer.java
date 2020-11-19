package number.utils;

public class RefContainer<T>
{
    public RefContainer(T value)
    {
        this.value = value;
    }

    private T value;

    public T GetValue()
    {
        return value;
    }

    public void SetValue(T value)
    {
        this.value = value;
    }
}
