package com.bitongchong.notebook.note12;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liuyuehe
 * @date 2020/6/20 21:31
 */
public class Main {
    /**
     * 输入一个map，对map中的所有value进行某个操作，最后以List的形式返回
     *
     * @param map -
     */
    public static List<Integer> getStream(Map<String, Integer> map) {
        List<Integer> collect = map.values().stream().map(m -> m = m + 233).sorted().collect(Collectors.toList());
        return collect;
    }

    /**
     * 输入一个list，对list中所有的值进行某个条件的过滤，最后整合成一个字符串输出
     *
     * @param list -
     * @return -
     */
    public static String listToString(List<String> list) {
        String collect = list.stream().filter(s -> !s.isEmpty()).collect(Collectors.joining("---"));
        return collect;
    }

    /**
     * 输入一个list（list中可以存各类对象），然后将需要统计满足某个条件的所有数字的总和
     *
     * @param list -
     * @return -
     */
    public static int calculateListSum(List<Integer> list) {
        int sum = list.stream().filter(integer -> integer >= 60).mapToInt(Integer::intValue).sum();
        return sum;
    }

    /**
     * 给两个具有相同字段的对象数组，在数组A中寻找数组B中对应相同字段的对象，然后对其进行赋值
     * 比如：对象A["id":1,"name":"tom","class":null]，对象B["id":1,"name":"tom-mark","class":"class-2"]
     * 需要以对象B为基础，更新对象A中所有的信息
     *
     * @param args -
     */
    public static void updateObjectList(List<Student> listA, List<Student> listB) {
        listA.forEach(a -> {
            List<Student> students = listB.stream().filter(b -> b.id == a.id).collect(Collectors.toList());
            if (students.size() > 0) {
                Student student = students.get(0);
                a.name = student.name;
                a.classInfo = student.classInfo;
            }

        });
        listA.forEach(System.out::println);
    }

    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        map.put("tom", 34);
        map.put("liu", 23);
        map.put("zhang", 223);
        List<Integer> list = Main.getStream(map);
        list.forEach(System.out::println);
        List<String> strings = Arrays.asList("abc", "", "bc", "efg", "abcd", "", "jkl");
        System.out.println(Main.listToString(strings));

        System.out.println("---计算一个整数List中所有满足某个条件的元素值的和");
        List<Integer> list1 = Arrays.asList(12, 23, 67, 62, 100);
        System.out.println(Main.calculateListSum(list1));

        System.out.println("---提供ListA和ListB两个对象列表，以ListB为基础来更新ListA中满足条件的元素");
        Main.updateObjectList(
                Arrays.asList(
                        new Student(1, "tom", ""),
                        new Student(2, "lily", ""),
                        new Student(4, "zhang", "")
                ),
                Arrays.asList(
                        new Student(1, "tom-plus", "class-1"),
                        new Student(2, "lily-plus", "class-2"),
                        new Student(3, "li-plus", "class-22")
                )
        );
    }

    @Data
    @AllArgsConstructor
    static class Student {
        private int id;
        private String name;
        private String classInfo;
    }
}
