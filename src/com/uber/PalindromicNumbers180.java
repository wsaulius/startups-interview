package src.com.uber;

import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

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

    static final List<Integer> convertibleDigits = Stream.of(0, 1, 6, 8, 9).collect(Collectors.toList());

    static final List<Integer> convertibles =
            convertibleDigits.stream().map(upDownDigit -> 48 + upDownDigit).sorted().collect(Collectors.toList());

    public static Predicate<Integer> is180DegreePalindrome() {
        return Solution::test;
    }

    private static boolean test(Integer digit) {
        // Convert to Integers
        final List<WeakReference<Integer>> asList =
                Integer.toString(digit).chars().boxed().map(WeakReference::new)
                        .collect(Collectors.toList());

        ListIterator<WeakReference<Integer>> listIterator = asList.listIterator();

        // Memoizing attempt
        final Map<Integer, Integer> matchTable = new WeakHashMap<>(asList.size());

        // Move iterator to end
        while (listIterator.hasNext()) {
            matchTable.putIfAbsent(listIterator.next().get(), 0);
        }

        for (int i = 0; listIterator.hasPrevious(); i++) {

            final Integer intFromBack = requireNonNull(listIterator.previous().get());
            if (convertibles.contains(intFromBack)) {

                switch (intFromBack) {

                    case 54:
                    case 57:

                        // Quick cases
                        if (asList.size() == 1) {
                            System.out.printf("%s is %s.\n\n", digit, STATE_AS_SAME);
                            return true;
                        }

                        int intGap = Math.abs(intFromBack - requireNonNull(asList.get(i).get()));

                        // Another quick case
                        if (intGap == 0) {
                            System.out.printf("%s is %s.\n\n", digit, STATE_DIFF);
                            return false;
                        }

                        // Difference between '6' and '9'
                        if (intGap == 3) {

                            // 6 - 9 case
                            matchTable.computeIfPresent(requireNonNull( asList.get(i).get()), (key, value) -> value + 1);

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

                        if (intFromBack.compareTo(requireNonNull(asList.get(i).get())) == 0) {

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

        boolean isPalindromic = allUnmatched <= 1 && !matchTable.containsValue(-1);

        System.out.printf("%s is %s.\n\n", digit, ((isPalindromic) ? STATE_AS_SAME : STATE_DIFF));
        return isPalindromic;
    }

    public static void main(String[] args) {

        Random random = new Random( 4 );
        final List<Integer> testBigNumbers = new LinkedList<>();

        convertibleDigits.forEach( upDownDigit ->
                testBigNumbers.add(new BigInteger(String.valueOf(upDownDigit)
                        .repeat( Math.abs( random.nextInt( 10 ) ) ) ).intValue() ) );

        Stream<Integer> upTo50 = IntStream.rangeClosed(1, 50).boxed();

        Stream<Integer> otherCases = Stream.of(69, 616, 919, 96161, 96896, 1968961, 88619, 8861988,
                1967, 1961, 19061, 190061, 707, 808, 8001, 1001, 101, 110, 8800088,
                818, 90, 88, 99, 66, 303).sorted();

        final Stream<Integer> mergedStream =
                Stream.of( upTo50, otherCases, testBigNumbers.stream() )
                        .flatMap( integer -> integer ).sorted();

        // Print Convertibles tested
        mergedStream.forEach( digit -> is180DegreePalindrome().test(digit));

    }

}
