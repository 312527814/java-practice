package com.my;

public class Node {
    public String key;
    public String value;
    public Node next;

    public static void main(String[] args) {
//        while(null != e) {
//            Entry<K,V> next = e.next;//第一行，线程1执行到此被调度挂起
//            int i = indexFor(e.hash, newCapacity);//第二行
//            e.next = newTable[i];//第三行
//            newTable[i] = e;//第四行
//            e = next;//第五行
//        }


        Node e = new Node();
        e.key="1";
        e.value="value1";
        ss(e);
        System.out.println(e);

    }
    public static void ss(Node e){
        Node[] table = new Node[10];
        Node next = e.next;
        e.next=table[0];
        table[0]=e;
        e = next;//第五行
        System.out.println("333");
    }
}
