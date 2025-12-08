package com.my;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
//        assertTrue( true );
//        IntStream.range(1, 12).peek(x -> System.out.print("\nA" + x));
//        System.out.println("..");
//
        IntStream.range(1, 10)


                .peek(x -> System.out.print("\nAe" + x))
                .skip(3)
                .peek(x -> System.out.print("B" + x))

                .forEach(x -> System.out.print("C" + x));


//        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
//        numbers.stream()
//                .peek(System.out::println);
//        List<Integer> result = numbers.stream()
//                .peek(System.out::println) // 打印每个元素
//                .map(n -> n * 2) // 将每个元素乘以2
//                .collect(Collectors.toList());


//        Stream.of("one", "two", "three", "four")
//                .filter(e -> e.length() > 3)
//                .peek(e -> System.out.println("Filtered value: " + e))
//                .map(String::toUpperCase)
//                .peek(e -> System.out.println("Mapped value: " + e))
//                .collect(Collectors.toList());

    }
}
