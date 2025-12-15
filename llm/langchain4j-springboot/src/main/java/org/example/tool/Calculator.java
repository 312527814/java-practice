package org.example.tool;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class Calculator
{
    @Tool("Calculates the length of a string")
    int stringLength(String s) {
        System.out.println("Called stringLength with s='" + s + "'");
        return s.length();
    }

    @Tool("计算两个数的和")
    int add(int a, int b) {
        System.out.println("Called add with a=" + a + ", b=" + b);
        return a + b;
    }

    @Tool("Calculates the square root of a number")
    double sqrt(int x) {
        System.out.println("Called sqrt with x=" + x);
        return Math.sqrt(x);
    }

    @Tool("拼接两个字符串，直接返回结果，模型就不要加多余的说明了。")
    String pinjie(String a,String b){
        System.out.println("拼接两个字符串");
        return  a+":"+b;
    }
}
