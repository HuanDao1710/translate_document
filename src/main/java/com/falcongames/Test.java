package com.falcongames;

import com.falcongames.utils.TranslatorUtil;

import java.io.IOException;

public class Test {



    public static void main(String[] args) throws IOException {
        System.out.println("Begin Test:");
        System.out.println("-------------");
//        String[] s = "hello@a".split("@");
//        System.out.println(s.length);

        System.out.println(TranslatorUtil.translate("en", "vi", "hello"));
        System.out.println("--------------");
        //System.out.println(List.of("hello".split("\\.")));

    }
}
