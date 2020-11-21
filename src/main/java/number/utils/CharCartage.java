package number.utils;

import java.util.Objects;

public class CharCartage {
    public char a;
    public char b;


    public CharCartage(char c1, char c2) {
        this.a = c1;
        this.b = c2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CharCartage)
            return Objects.equals(this.a, ((CharCartage) obj).a) && Objects.equals(this.b, ((CharCartage) obj).b);
        else return super.equals(obj);
    }
}


