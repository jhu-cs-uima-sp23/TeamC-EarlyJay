package com.example.a5_sample;
import java.lang.String;


public class Park {
    private String name;
    private int number;
    private boolean checked_in;

    public Park(String name, int number) {
        this.name = name;
        this.number = number;
        this.checked_in = false;
    }

    public String getname() { return name; }

    public int getnumber() { return number; }

    public boolean getchecked_in() { return checked_in; }

    public void setchecked_in(boolean a) {
        if (a != checked_in) {
            checked_in = a;
            if (a) number++;
            else number--;
        }
    }
}
