package ru.example.forkjoinframework;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MainApp {

    static class AggregationCounterTask extends RecursiveTask<Integer> {
        private static final int THRESHOLD = 10;
        private final List<Item> items;

        public AggregationCounterTask(List<Item> items) {
            this.items = items;
        }

        @Override
        protected Integer compute() {
            if (items.size() <= THRESHOLD) {
                int count = 0;
                for (Item item : items) {
                    if (item.isAggregated()) {
                        count++;
                    }
                }
                System.out.println("[" + Thread.currentThread().getName() + "] Обработано " + items.size() +
                        " элементов. Найдено агрегированных: " + count);
                return count;
            } else {
                int mid = items.size() / 2;
                AggregationCounterTask leftTask = new AggregationCounterTask(items.subList(0, mid));
                AggregationCounterTask rightTask = new AggregationCounterTask(items.subList(mid, items.size()));

                leftTask.fork();

                int rightResult = rightTask.compute();
                
                int leftResult = leftTask.join();

                return leftResult + rightResult;
            }
        }
    }

    public static void main(String[] args) {
        List<Item> items = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i <= 1000; i++) {
            boolean success = random.nextInt(10) < 7;
            items.add(new Item("Item-" + i, success));
        }

        System.out.println("Всего элементов для обработки: " + items.size());
        
        ForkJoinPool pool = new ForkJoinPool();
        
        System.out.println("\nЗапуск ForkJoin задачи");
        long start = System.currentTimeMillis();

        AggregationCounterTask task = new AggregationCounterTask(items);
        int aggregatedCount = pool.invoke(task);

        long end = System.currentTimeMillis();
        
        System.out.println("\nРезультаты");
        System.out.println("Успешно агрегировано элементов: " + aggregatedCount + " из " + items.size());
        System.out.println("Время выполнения: " + (end - start) + " мс");

        pool.shutdown();
    }
}