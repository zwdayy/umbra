package com.umbra.util;

import android.os.Looper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangweiding on 15/12/9.
 */
public class ValidateUtil {

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }


    public static boolean validateMobile(final String str) { // 验证手机号
        if (validateEmpty(str)){
            return false;
        }
        Pattern pattern = Pattern.compile("^[1][3,4,5,7,8,9][0-9]{9}$");
        return pattern.matcher(str).matches();
    }

    public static boolean validateNumeric(final String str) { // 验证数字
        if (validateEmpty(str)){
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static boolean validateAlphabets(final String str) { // 验证字母
        if (validateEmpty(str)){
            return false;
        }
        Pattern pattern = Pattern.compile("[A-Za-z]*");
        return pattern.matcher(str).matches();
    }

    public static boolean validateInteger(final String str) { // 验证整数
        if (validateEmpty(str)){
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static boolean validatePassword(final String str) {
        if (validateEmpty(str)){
            return false;
        }
        Pattern pattern = Pattern.compile("^[0-9A-Za-z]{6,20}$");
        return pattern.matcher(str).matches();
    }

    public static boolean validatePass(String pass){
//        return true;
        int count = 0;
        Pattern pattern1 = Pattern.compile("[\\d]+");
        Pattern pattern2 = Pattern.compile("[@#$%&*()_]+");
        Pattern pattern3 = Pattern.compile("[a-zA-Z]+");
        if (pattern1.matcher(pass).find()){
            count ++;
        }
        if (pattern2.matcher(pass).find()){
            count ++;
        }
        if (pattern3.matcher(pass).find()){
            count ++;
        }
        return count>=2;
    }

    public static boolean validateEmail(final String str) {
        Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        return pattern.matcher(str).matches();
    }

    public static boolean validateEmpty(String str) {
        return str == null || str.length() == 0 || "null".equalsIgnoreCase(str);
    }

    public static boolean validateName(String realName){
        Pattern pattern = Pattern.compile("^[a-zA-Z\\u4e00-\\u9fa5]+$");
        return pattern.matcher(realName).matches();
    }

    public static boolean validateString(String str) {
        return str != null && !"".equals(str.trim());
    }

    public static boolean validateCarNumber(String carNumber) {
        if (validateEmpty(carNumber)){
            return false;
        }
        Pattern pattern = Pattern.compile("^[\\u4e00-\\u9fa5]" +
                "[a-zA-Z]" +
                "[a-zA-Z_0-9]{4}" +
                "[a-zA-Z_0-9_\\u4e00-\\u9fa5]$");
        return pattern.matcher(carNumber).matches();
    }

    /**
     * 认证证件号码规定，英文
     * @param cardNumber 证件号
     * @return true验证合规
     */
    public static boolean validateCardNumber(String cardNumber){
        if (validateEmpty(cardNumber)){
            return false;
        }
        Pattern pattern = Pattern.compile("^[0-9A-Za-z]+$");
        return pattern.matcher(cardNumber).matches();
    }

    /**
     * 验证身份证号，根据官方算法计算身份证号是否真实
     * @param idNumber 身份证号
     * @return true为验证合规
     *
     * 113578843333322157  ？
     */
    public static boolean validateIDCard(String idNumber){
        if (validateEmpty(idNumber) || idNumber.length() != 18){
            return false;
        }
        try{
            int month = Integer.parseInt(idNumber.substring(10,12));
            int day = Integer.parseInt(idNumber.substring(12,14));
            if(month < 1 || month > 12 || day < 1 || day > 31){
                return false;
            }
            int product = 0;
            //检验数组
            String[] check = new String[]{"1","0","x","9","8","7","6","5","4","3","2"};
            //最后验算出来的号码
            String code;
            //最后一位号码
            String lastNumber = String.valueOf(idNumber.charAt(17));
            for (int index = 0; index < 17; index++){
                product += Integer.parseInt(String.valueOf(idNumber.charAt(index)))
                        * Math.pow(2, 17 - index);
            }
            product = product % 11;
            code = check[product];
            return code.equals(lastNumber)
                    || (code.equals("x") && lastNumber.equals("X")) ;
        }catch (NumberFormatException e){
            //数字解析错误说明输入不对，也返回false
            return false;
        }
    }
    
    /**
     * 匹配中文正则表达式
     */
    public static boolean validateChinese(String str) {
        if (validateEmpty(str)){
            return false;
        }
        Pattern p = Pattern.compile("^[\u4e00-\u9fa5]$");
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 校验银行卡卡号
     */
    public static boolean checkBankCard(String cardId) {
        if (validateEmpty(cardId)) {
            return false;
        }
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        return bit != 'N' && cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     */
    private static char getBankCardCheckCode(String nonCheckCodeCardId){
        if(nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if(j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');
    }

    /**
     * 判断字符串是否是整数
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是浮点数
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            if (value.contains("."))
                return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是数字
     */
    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value);
    }
}