package StreamAPI;

import NIO.NIOServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class StreamApp {
    static class Person {
        enum Position {
            ENGINEER, DIRECTOR, MANAGER;
        }

        private String name;
        private int age;
        private Position position;

        public Person(String name, int age, Position position) {
            this.name = name;
            this.age = age;
            this.position = position;
        }
    }

    private static void streamSimpleTask() {
        List<Person> persons = new ArrayList<>(Arrays.asList(
                new Person("Bob1", 35, Person.Position.MANAGER),
                new Person("Bob2", 44, Person.Position.DIRECTOR),
                new Person("Bob3", 25, Person.Position.ENGINEER),
                new Person("Bob4", 42, Person.Position.ENGINEER),
                new Person("Bob5", 55, Person.Position.MANAGER),
                new Person("Bob6", 19, Person.Position.MANAGER),
                new Person("Bob7", 33, Person.Position.ENGINEER),
                new Person("Bob8", 37, Person.Position.MANAGER)
        ));

        List<String> engineersNames = persons.stream()
                .filter(person -> person.position == Person.Position.ENGINEER)
                .sorted((o1, o2) -> o1.age - o2.age)
                .map((Function<Person, String>) person -> person.name)
                .collect(Collectors.toList());
        System.out.println(engineersNames);
    }
    public static void main(String[] args) {
        streamSimpleTask();
//        System.out.println("--------------------------------------------");
 //       List<String> list = new ArrayList<>(Arrays.asList("A", "AB", "B"));
  //      Stream<String> stream = list.stream().forEach(System.out::println);
        IntStream intStream = IntStream.of(1, 2, 3, 4);
        LongStream longStream = LongStream.of(1L, 2L, 3L, 4L);
        IntStream rangedIntStream = IntStream.rangeClosed(1, 100);
        //IntStream intStream = Stream.of(1, 2, 3, 4).mapToInt(n -> n);

/*        Stream<String> stream = Stream.of("A", "B", "C");
        List<String> list = stream.collect(Collectors.toList());
        Set<String> set = stream.collect(Collectors.toSet());
*/
        System.out.println("--------------------------------------------");
        String[] array = {"Aaa", "Bbbbb", "Cc"};
        System.out.println(Arrays.stream(array)
                .collect(Collectors.averagingInt(s -> s.length())));

        System.out.println("--------------------------------------------");
        String[] array1 = {"Aaa", "Bbbbb", "Cc", "Aa"};
        System.out.println(Arrays.stream(array1)
                .filter(str -> str.startsWith("A"))
                .collect(Collectors.joining(" и ", "Перечисленные слова [", "] начинаются на букву A")));

        System.out.println("--------------------------------------------");
        Stream<String> stream1 = Stream.of("A", "B", "C");
        stream1.forEach(str -> System.out.println(str));
        Stream<String> stream2 = Stream.of("A", "B", "C");
        stream2.forEach(System.out::println);

        System.out.println("--------------------------------------------");
        Stream<Integer> stream = Stream.of(1, 2, 3, 24, 5, 6);
        stream.reduce((i1, i2) -> i1 > i2 ? i1 : i2)
                .ifPresent(System.out::println);

        System.out.println("--------------------------------------------");
        Stream<Integer> stream3 = Stream.of(1, 2, 3, 4, 5, 6);
        stream3.filter(n -> n % 2 == 0).forEach(System.out::println);

        System.out.println("--------------------------------------------");
        Stream.of("A", "A", "A", "B", "B", "B", "B")
                .distinct()
                .forEach(System.out::println);

        System.out.println("--------------------------------------------");
        Stream<String> stream4 = Stream.of("Java", "Core", "ABC");
        stream4.map(str -> str.length()).forEach(System.out::println);

        System.out.println("--------------------------------------------");
        Stream.of("1", "2", "3", "4", "5")
                .filter(s -> {
                    System.out.println("Фильтр: " + s);
                    return true;
                })
                .forEach(s -> System.out.println("Результат: " + s));
        System.out.println("--------------------------------------------");
        Stream.of("dd2", "aa2", "bb1", "bb3", "cc4")
                .map(s -> {
                    System.out.println("map: " + s);
                    return s.toUpperCase();
                })
                .filter(s -> {
                    System.out.println("filter: " + s);
                    return s.startsWith("A");
                })
                .forEach(s -> System.out.println("forEach: " + s));

        System.out.println("--------------------------------------------");
        Stream.of("dd2", "aa2", "bb1", "bb3", "cc4")
                .filter(s -> {
                    System.out.println("filter: " + s);
                    return s.startsWith("a");
                })
                .map(s -> {
                    System.out.println("map: " + s);
                    return s.toUpperCase();
                })
                .forEach(s -> System.out.println("forEach: " + s));

        System.out.println("--------------------------------------------");
        Stream<String> stream5 = Stream.of("a1", "b2", "a3", "c4", "d5")
                .filter(s -> s.startsWith("d"));
        stream5.anyMatch(s -> true);     // Пройдёт без проблем
        //stream5.noneMatch(s -> true);    // Выдаст исключение

        System.out.println("--------------------------------------------");
        Arrays.asList("a1", "a2", "b1", "c2", "c1")
                .parallelStream()
                .filter(s -> {
                    System.out.format("filter: %s [%s]\n",
                            s, Thread.currentThread().getName());
                    return true;
                })
                .map(s -> {
                    System.out.format("map: %s [%s]\n",
                            s, Thread.currentThread().getName());
                    return s.toUpperCase();
                })
                .forEach(s -> System.out.format("forEach: %s [%s]\n",
                        s, Thread.currentThread().getName()));

        System.out.println("--------------------------------------------");
        Arrays.asList("a1", "a2", "b1", "c2", "c1")
                .parallelStream()
                .filter(s -> {
                    System.out.format("filter: %s [%s]\n",
                            s, Thread.currentThread().getName());
                    return true;
                })
                .map(s -> {
                    System.out.format("map: %s [%s]\n",
                            s, Thread.currentThread().getName());
                    return s.toUpperCase();
                })
                .sorted((s1, s2) -> {
                    System.out.format("sort: %s <> %s [%s]\n",
                            s1, s2, Thread.currentThread().getName());
                    return s1.compareTo(s2);
                })
                .forEach(s -> System.out.format("forEach: %s [%s]\n",
                        s, Thread.currentThread().getName()));

        System.out.println("--------------------------------------------");

        System.out.println("--------------------------------------------");
        System.out.println("--------------------------------------------");
        System.out.println("--------------------------------------------");
    }



}