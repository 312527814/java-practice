package com.my.fegin;

import feign.Feign;
import feign.codec.StringDecoder;

import java.util.List;

public class MyApp {
    public static void main(String... args) {
        GitHub github = Feign.builder()
                .decoder(new StringDecoder())
                .target(GitHub.class, "https://api.github.com");

        // Fetch and print a list of the contributors to this library.
        String  contributors= github.contributors("OpenFeign", "feign");
        System.out.println(contributors);
//        for (Contributor contributor : contributors) {
//            System.out.println(contributor.login + " (" + contributor.contributions + ")");
//        }
    }
}
