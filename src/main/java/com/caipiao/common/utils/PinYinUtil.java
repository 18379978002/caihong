package com.caipiao.common.utils;

import cn.hutool.core.codec.Base64;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtil {

    public static final String pattern = "[\\u4E00-\\u9FA5]+";
    /**
     * 测试main方法
     * @param args
     */
    public static void main(String[] args) {
        byte[] decode = Base64.decode("NTgsNjA=");

        String s = new String(decode);

        System.out.println(s);

    }
    /**
     * 获取字符串拼音的第一个字母
     * @param chinese
     * @return
     */
    public static String toFirstChar(String chinese){
        String pinyinStr = "";
        char[] newChar = removeNonBmpUnicodes(chinese).toCharArray();  //转为单个字符
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {
                try {
                    pinyinStr += PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0].charAt(0);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }else{
                pinyinStr += newChar[i];
            }
        }
        return pinyinStr.toUpperCase();
    }

    /**
     * 汉字转为拼音
     * @param chinese
     * @return
     */
    public static String toPinyin(String chinese){
        String pinyinStr = "";
        char[] newChar = removeNonBmpUnicodes(chinese).toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {
                try {
                    pinyinStr += PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0];
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }else{
                pinyinStr += newChar[i];
            }
        }
        return pinyinStr.toUpperCase();
    }

    public static String removeNonBmpUnicodes(String s) {
        return null == s ? null : s.replaceAll("[^\\u4E00-\\u9FA5]", "");
    }
}
