package com.caipiao.common.utils;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiaoyinandan
 * @date 2022/2/26 下午10:24
 */
@UtilityClass
public class ExtendMath {

    public static void main(String[] args) {

        //排列三 晚上8点

        long nchoosek = nchoosek(70, 25);
        long nchoosek1 = nchoosek(5, 2);
        System.out.println(nchoosek);
        System.out.println(nchoosek1);
        System.out.println(nchoosek1*nchoosek);


        int[] array = new int[]{1,2,3,4,5,6,7};

        System.out.println(Json.toJsonString(nchoosek(array, 3)));

        List<List<String>> ls = new ArrayList<>();
        ls.add(Arrays.asList("2"));
        ls.add(Arrays.asList("4"));
        ls.add(Arrays.asList("6"));

        List<String> all = findAll(ls);

        System.out.println(Json.toJsonString(all));
        System.out.println(all.size());

        long nchoosek2 = nchoosek(36, 8);
        System.out.println(nchoosek2);


    }



    public List<String> getResult(List<String> arrA, List<String> arrB) {

        if (arrA.size() == 0) {
            return arrB;
        }
        if (arrB.size() == 0) {
            return arrA;
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i < arrA.size(); i++) {
            for (int j = 0; j < arrB.size(); j++) {
                result.add(arrA.get(i) + arrB.get(j));
            }
        }
        return result;
    };


    public List<String> findAll(List<List<String>> arr) {
        return arr.stream().reduce(new ArrayList<>(), ExtendMath::getResult);
    }




    public static int[][] nchoosek(int[] array, int k){
        int n = array.length;
        checknk(n, k);
        if(k == 0){
            return new int[1][0];
        }
        return nchoosek0(array, n, k);
    }

    private static int[][] nchoosek0(int[] array, int n, int k){
        int[][] comb = null;
        if(n == k){
            comb = new int[1][n];
            for(int i = 0; i < n; i++){
                comb[0][i] = array[i];
            }
            return comb;
        }
        if(k == 1){
            comb = new int[n][1];
            for(int i = 0; i < n; i++){
                comb[i][0] = array[i];
            }
            return comb;
        }
        for(int i = 0, limit = n - k + 1; i < limit; i++){
            int[][] next = nchoosek0(Arrays.copyOfRange(array, i + 1, n), n - i - 1, k - 1); // Get all possible values for the next one
            int combRowLen = comb == null ? 0 : comb.length;
            int totalRowLen = next.length + combRowLen;
            int totalColLen = next[0].length + 1;
            int[][] tempComb = new int[totalRowLen][totalColLen];
            if(comb != null){ // TempComb capacity expansion comb
                for(int j = 0; j < combRowLen; j++){
                    tempComb[j] = Arrays.copyOf(comb[j], totalColLen);
                }
            }
            int value = array[i];
            for(int row = combRowLen; row < totalRowLen; row++){
                tempComb[row][0] = value; // The value completes the current one
                for(int col = 1; col < totalColLen; col++){ // Copy the next one.
                    tempComb[row][col] = next[row - combRowLen][col - 1];
                }
            }
            comb = tempComb;
        }
        return comb;
    }

    public static long nchoosek(int n, int k){
        if(n > 70 || (n == 70 && k > 25 && k < 45)){
            throw new IllegalArgumentException("N(" + n + ") and k(" + k + ") don't meet the requirements.");
        }
        checknk(n, k);
        k = k > (n - k) ? n - k : k;
        if(k <= 1){  // C(n, 0) = 1, C(n, 1) = n
            return k == 0 ? 1 : n;
        }
        int[] divisors = new int[k]; // n - k + 1 : n
        int firstDivisor = n - k + 1;
        for(int i = 0; i < k; i++){
            divisors[i] = firstDivisor + i;
        }
        outer:
        for(int dividend = 2; dividend <= k; dividend++){
            for(int i = k - 1; i >= 0; i--){
                int divisor = divisors[i];
                if(divisor % dividend == 0){
                    divisors[i] = divisor / dividend;
                    continue outer;
                }
            }
            int[] perms = factor(dividend);
            for(int perm : perms){
                for(int j = 0; j < k; j++){
                    int divisor = divisors[j];
                    if(divisor % perm == 0){
                        divisors[j] = divisor / perm;
                        break;
                    }
                }
            }
        }
        long cnk = 1L;
        for(int i = 0; i < k; i++){
            cnk *= divisors[i];
        }
        return cnk;
    }


    public static int[] factor(int n){
        if(n < 0){
            throw new IllegalArgumentException("N must be a non negative integer.");
        }
        if(n < 4){
            return new int[]{n};
        }
        int factorNums = (int)(Math.log(Integer.highestOneBit(n)) / Math.log(2));
        int[] factors = new int[factorNums];
        int factorCount = 0;
        for(int i = 2; i <= (int) Math.sqrt(n); i++){
            if(n % i == 0){
                factors[factorCount++] = i;
                n /= i;
                i = 1;
            }
        }
        factors[factorCount++] = n;
        return Arrays.copyOf(factors, factorCount);
    }


    private static void checknk(int n, int k){
        if(k < 0 || k > n){ // N must be a positive integer.
            throw new IllegalArgumentException("K must be an integer between 0 and N.");
        }
    }

}
