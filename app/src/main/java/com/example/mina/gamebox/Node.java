package com.example.mina.gamebox;

import android.util.Pair;

public class Node{
    int value , HOrder , VOrder , height;
    public Node right , left;

    public Node(int value)
    {
        this.value = value;
        height = 1;
        right = left = null;
    }

    public int getValue() {
        return value;
    }

    public int getHOrder() {
        return HOrder;
    }

    public int getVOrder() {
        return VOrder;
    }

    public void setHOrder(int HOrder) {
        this.HOrder = HOrder;
    }

    public void setVOrder(int VOrder) {
        this.VOrder = VOrder;
    }


}
