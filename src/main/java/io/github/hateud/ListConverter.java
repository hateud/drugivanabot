package io.github.hateud;

import java.util.ArrayList;
import java.util.Arrays;

public class ListConverter {
    public static String toStr(String[] list){
        return String.join(",", list);
    }

    public static String[] toList(String str){
        return str.split(",");
    }
}
