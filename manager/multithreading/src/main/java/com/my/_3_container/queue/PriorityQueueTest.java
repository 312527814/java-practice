package com.my._3_container.queue;

import org.junit.Test;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class PriorityQueueTest {
    public static void main(String[] args) {

        PriorityQueue<String> priorityQueue = new PriorityQueue();
        priorityQueue.add("a");
        priorityQueue.add("c");
        priorityQueue.add("b");
        priorityQueue.add("e");
        priorityQueue.add("d");

        priorityQueue.add("f");
    }

    @Test
    public void test1() {
        PriorityQueue<Integer> priorityQueue = new PriorityQueue();
        priorityQueue.add(3);
        priorityQueue.add(5);
        priorityQueue.add(2);
        priorityQueue.add(9);
        priorityQueue.add(7);
        priorityQueue.add(15);
        priorityQueue.add(11);
        priorityQueue.add(13);
        priorityQueue.add(20);
        priorityQueue.add(4);

    }

    @Test
    public void test2() {
        PriorityQueue<Integer> priorityQueue = new PriorityQueue();
        priorityQueue.add(3);
        priorityQueue.add(4);
        priorityQueue.add(6);
        priorityQueue.add(9);
        priorityQueue.add(5);
        priorityQueue.add(15);
        priorityQueue.add(11);
        priorityQueue.add(13);
        priorityQueue.add(20);
        priorityQueue.add(7);

        priorityQueue.remove(7);
        Integer poll = priorityQueue.poll();
        Iterator<Integer> iterator = priorityQueue.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            System.out.println(next);
        }


    }


    public void ss() throws InterruptedException {


        DelayQueue<Delayed> delayQueue=new DelayQueue<>();
        delayQueue.take();
    }

    static class Task implements Delayed {
        long time = System.currentTimeMillis();
        public Task(long time) {
            this.time = time;
        }
        @Override
        public int compareTo(Delayed o) {
            if(this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS))
                return -1;
            else if(this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS))
                return 1;
            else
                return 0;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(time - System.currentTimeMillis(),TimeUnit.MILLISECONDS);
        }
        @Override
        public String toString() {
            return "" + time;
        }
    }


}
