package practice1.common;

public class Validator {

    public static void validate(int[] nums, int first, int last) {
        if (nums == null || nums.length == 0) {
            throw new RuntimeException("Array should not be empty or null");
        }

        if (last > nums.length) {
            throw new RuntimeException(
                    "Last ["+ last + "] should not be greater than nums.length = " + nums.length);
        }

        if (first < 0) {
            throw new RuntimeException("First ["+ first + "] should not be less than zero");
        }

        if (first >= last) {
            throw new RuntimeException("First should be less than last");
        }
    }
}
