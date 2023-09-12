package practice1.min.impl;

import practice1.min.Finder;

import static practice1.common.Sleeper.sleep;
import static practice1.common.Validator.validate;

public class SyncFinder implements Finder {
    private final int[] nums;
    private final int first;
    private final int last;

    public SyncFinder(int[] nums,
                      int first,
                      int last) {
        validate(nums, first, last);
        this.nums = nums;
        this.first = first;
        this.last = last;
    }

    public SyncFinder(int[] nums) {
        this.nums = nums;
        this.first = 0;
        this.last = nums.length;
    }

    @Override
    public int min() {
        int min = nums[first];
        for (int i = first; i < last; ++i ) {
            sleep(1);
            if (nums[i] < min) {
                min = nums[i];
            }
        }

        return min;
    }

    public static void main(String[] args) {
        System.out.println(new SyncFinder(new int[]{1, 2, 3}).min() == 1);
        System.out.println(new SyncFinder(new int[]{1, -2, 3}).min() == -2);
        System.out.println(new SyncFinder(new int[]{-1, 2, 3}).min() == -1);
        System.out.println(new SyncFinder(new int[]{1, 2, -100}).min() == -100);
        System.out.println(new SyncFinder(new int[]{2, 2, 2}).min() == 2);
    }
}
