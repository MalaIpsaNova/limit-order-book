package com.mizuho.lob;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Order
{
    private long id;
    private double price;
    private char side;
    private long size;
}
