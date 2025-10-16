import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Jdk21Test {

    @Test
    void testVirtualThreads() {
        testPlatformThreads(2000);
        testPlatformThreads(20_000);

        testVirtualThreads(2000);
        testVirtualThreads(20_000);
    }

    void testPlatformThreads(int maximum) {
        long time = System.currentTimeMillis();

        try (var executor = Executors.newCachedThreadPool()) {
            IntStream.range(0, maximum).forEach(i -> {
                executor.submit(() -> {
                    Thread.sleep(Duration.ofSeconds(1));
                    return i;
                });
            });
        }

        time = System.currentTimeMillis() - time;
        System.out.println("Number of platform threads = " + maximum + ", Duration(ms) = " + time);
    }

    void testVirtualThreads(int maximum) {
        long time = System.currentTimeMillis();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, maximum).forEach(i -> {
                executor.submit(() -> {
                    Thread.sleep(Duration.ofSeconds(1));
                    return i;
                });
            });
        }

        time = System.currentTimeMillis() - time;
        System.out.println("Number of virtual threads = " + maximum + ", Duration(ms) = " + time);
    }


    @Test
    public void testSequencedCollection() {
        List<String> sc = Stream.of("Alpha", "Bravo", "Charlie", "Delta").
                collect(Collectors.toCollection(ArrayList::new));
        System.out.println("Initial list: " + sc);
        System.out.println("Reversed list: " + sc.reversed());
        System.out.println("First item: " + sc.getFirst());
        System.out.println("Last item: " + sc.getLast());
        sc.addFirst("Before Alpha");
        sc.addLast("After Delta");
        System.out.println("Added new first and last item: " + sc);
    }
}

/*
G1:
    Default GC in JDK 9 and later, making it widely adopted and tested in a variety of applications.
    Suitable for general-purpose server applications, including enterprise systems, microservices,
    and web applications.

ZGC:
    Production-ready in JDK 15.
    ZGC is becoming increasingly popular for use cases where low-latency is a must, especially in industries like
    financial services, gaming, and real-time analytics.

Generational ZGC:
    JDK 21.
    The heap is divided into different "generations" based on the lifespan of objects.
    The idea is that most objects are short-lived, so collecting young objects (in the young generation)
    can be done frequently and quickly, while long-lived objects (in the old generation) are
    collected less frequently.
*/
