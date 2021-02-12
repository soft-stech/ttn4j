package ru.stech.ttn4j.number.utils;


import lombok.*;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GlobalValue
{
    private Long value;

    private Long currentLevel;

    private Boolean isCompleted = false;

    public GlobalValue() {
    }

    protected boolean canEqual(final Object other) {
        return other instanceof GlobalValue;
    }

}
