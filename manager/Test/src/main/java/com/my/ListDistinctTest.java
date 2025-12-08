package com.my;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 描 述: <br/>
 * 作 者: liuliang14<br/>
 * 创 建:2022年05月30⽇<br/>
 * 版 本:v1.0.0<br> *
 * 历 史: (版本) 作者 时间 注释 <br/>
 */
public class ListDistinctTest {
    public static void main(String[] args) {
        ArrayList<P> ps = new ArrayList<>();
        ps.add(new P("zhangsan",10));
        ps.add(new P("zhangsan",11));
        ps.add(new P("lis",11));
        ps.add(new P("zhangsan",12));
        List<Integer> ages = ps.stream().filter(f -> f.getName().equals("zhangsan")).map(m -> m.getAge()).filter(age -> age > 10).collect(Collectors.toList());


        ArrayList<P> ps2 = new ArrayList<>();
        ps2.add(new P("zhangsan",11));
        ps2.add(new P("zhangsan",12));
        ps2.add(new P("lis",11));
        ps2.add(new P("zhangsan",12));
        List<P> collect2 = ps2.stream().filter(distinctByKey(o -> o.getName())).collect(Collectors.toList());


    }
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    static class P{
        private String name;
        private Integer age;

        public P(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}
