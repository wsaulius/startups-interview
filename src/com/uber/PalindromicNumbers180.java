package com.uber;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/*
Check if the number has this property: the number rotated 180 degrees equals its original value. Examples:
1. the number 88 rotated 180 degrees equals 88.
2. 181 rotated 180 degrees equals 181
3. 182 rotated 180 degrees does not hold the property

 Convertable numbers as agreed: 0, 1, 6, 8, 9
*/

// Main class should be named 'Solution'
class Solution {

    static final String STATE_AS_SAME = "OK, can be 180 degree rotated";
    static final String STATE_DIFF = "not convertible";

    static final List convertibles =
            IntStream.of(0, 1, 6, 8, 9).map(i -> 48 + i).sorted().boxed().collect(Collectors.toList());

    public static Predicate<Integer> is180DegreePalindrome() {
        return Solution::test;
    }

    private static boolean test(Integer digit) {

        // Convert to Integers
        final List<Integer> asList = Integer.toString(digit).chars().boxed().collect(Collectors.toList());

        ListIterator<Integer> iif = asList.listIterator();

        // Memoizing attempt
        final Map<Integer, Integer> matchTable = new HashMap<>(asList.size());

        // Move iterator to end
        while (iif.hasNext()) {
            matchTable.putIfAbsent(iif.next(), 0);
        }

        for (int i = 0; iif.hasPrevious(); i++) {

            final Integer intFromBack = iif.previous();
            if (convertibles.contains(intFromBack)) {

                switch (intFromBack) {

                    case 54:
                    case 57:

                        // Quick cases
                        int intGap = Math.abs(intFromBack - asList.get(i));

                        // Another quick case
                        if (intGap == 0) {
                            System.out.printf("%s is %s.\n\n", digit, STATE_DIFF);
                            return false;
                        }

                        // Difference between '6' and '9'
                        if (intGap == 3) {

                            // 6 - 9 case
                            matchTable.computeIfPresent(asList.get(i), (key, value) -> value + 1);
                            // Match the "other" side
                            matchTable.computeIfPresent(intFromBack, (key, value) -> value + 1);

                        }
                        break;

                    default:

                        // Quick cases
                        if (asList.size() == 1) {
                            System.out.printf("%s is %s.\n\n", digit, STATE_AS_SAME);
                            return true;
                        }

                        if (intFromBack.compareTo(asList.get(i)) == 0) {

                            matchTable.computeIfPresent(intFromBack, (key, value) -> value + 1);
                        } else {

                            matchTable.computeIfPresent(intFromBack, (key, value) -> -1);
                        }
                        break;
                }
            } else {

                matchTable.computeIfPresent(intFromBack, (key, value) -> -1);
            }

            // System.out.println(matchTable);
        }

        final long allUnmatched = matchTable.values()
                .stream().filter(integer -> (integer % 2 != 0)).count();

        boolean isPalindromic = !matchTable.containsValue(-1) && allUnmatched <= 1;

        System.out.printf("%s is %s.\n\n", digit, ((isPalindromic) ? STATE_AS_SAME : STATE_DIFF));
        return isPalindromic;
    }

    public static void main(String[] args) {

        Stream<Integer> upTo50 = IntStream.rangeClosed(0, 50).boxed();

        Stream<Integer> otherCases = Stream.of(69, 616, 919, 96161, 96896, 1968961, 88619, 8861988,
                1967, 1961, 19061, 190061, 707, 808, 8001, 1001, 101, 110, 8800088,
                818, 90, 88, 99, 66, 303).sorted();

        final Stream<Integer> mergedStream = Stream.of(upTo50, otherCases.parallel()).flatMap(r -> r).sorted();

        // Print Convertibles tested
        mergedStream.forEach(digit -> is180DegreePalindrome().test(digit));
    }
}

