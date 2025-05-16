package com.fib.cash_operations.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Denomination {

    private int amount;

    private int cashNote;

    @Override
    public String toString() {
        return amount + " x " + cashNote;
    }
}
