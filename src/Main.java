import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time

        int maxSize = 0; // результат - максимальный интервал среди всех строк
        int threadCount = 8;  // число потоков в пуле
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Integer>> list = new ArrayList<Future<Integer>>();

        System.out.println("Считаю результат в " + threadCount + " параллельных потоках...");
        for (String text : texts) {
            Callable<Integer> callable = new MyCallable(text); // создаем объект с аргументом - следующей строкой
            Future<Integer> future = executor.submit(callable); // запускаем задачу в пуле потоков
            list.add(future);
        }
        for (Future<Integer> future : list) {
            try {
                maxSize = Math.max(maxSize, future.get());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        System.out.println("Максимальный интервал среди всех строк: " + maxSize);
        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}