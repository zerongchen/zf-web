package com.aotain.zongfen.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DomainCheck {

    /**
     * 检查域名是否合法
     * @param domain
     * @return 返回 空 代表成功
     * 其余就返回不合法消息
     */
    public static String domainCheckLegal(String domain){

        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        String str = null;
        String reg =null;

        if(domain.indexOf(".")==-1){
            return "不符合网站格式";
        }
        if(".".equals(domain.substring(domain.length()-1,domain.length()))){
            return "不符合网站格式";
        }
        if(domain.startsWith(".")){
            return "不符合网站格式";
        }
        reg = "^\\d{0,}$";
        str = domain.substring(domain.lastIndexOf(".")+1,domain.length());
        p = Pattern.compile(reg);
        m = p.matcher(str);
        b = m.matches();

        if(b){
            return "顶级域名不符合规范";
        }
        String[] domins = domain.split("\\.");
        int valueLength = 0;
        for (int j =0 ;j< domins.length;j++) {
            String eachdomain = domins[j];

            //可以包含中文、字母a-z（大小写等价）、数字0-9或者半角的连接符"-"，"-"不能放在开头或结尾
            reg = "^(?!-.)(?!.*?-$)[-a-zA-Z0-9\\u4e00-\\u9fa5]*$";

            p = Pattern.compile(reg);
            m = p.matcher(eachdomain);
            b = m.matches();

            if (!b) {
                return "不符合域名注册规定";
            }

            str = m.group();

            reg = "^[\\u4e00-\\u9fa5]+$";//纯汉字必须大于1位
            p = Pattern.compile(reg);
            m = p.matcher(eachdomain);
            b = m.matches();
            if (b) {
                String chinese = m.group();
                if (chinese.length() < 2 || chinese.length() > 20) {
                    return "纯中文必须大于1个小于21个";
                }
            } else {
                //判断punycode长度
                if (str.length() < 2) {
                    return "长度必须大于等于2!";
                }else if(str.length() >= 3) {
                    //如果第一位、二位不是中文，就判断第三、四位是否是“-”
                    String str1 = str.substring(0, 3);
                    ;
                    String reg1 = "^[-a-zA-Z0-9]*$";
                    p = Pattern.compile(reg1);
                    m = p.matcher(str1);
                    b = m.matches();

                    if (b) {
                        if (str.indexOf("-") == 2 || str.indexOf("-") == 3) {
                            return " “-”符号不能出现在第三和第四位 ";
                        }
                    }

                    //判断输入的域名是否超长
                    String chinese = "[\u4e00-\u9fa5]";
                    for (int i = 0; i < str.length(); i++) {
                        String temp = str.substring(i, i + 1);
                        if (temp.matches(chinese)) {
                            valueLength += 2;
                        } else {
                            valueLength++;
                        }
                    }
                }
            }
        }
        if (valueLength > 63) {
            return "您输入的域名长度大于63位";
        }
        return "";
    }

    public static void main(String[] args){
        long start1 = System.currentTimeMillis();
        String regular = "^((http|https|ftp|rtsp|mms):(//|////){1}((([A-Za-z0-9_\\-:])+[.])|([A-Za-z0-9_\\-.:])+[.]){1,}(net|com|cn|org|cc|tv|[0-9]{1,3})(/S*/)((/S)+[.]{1}(jpg|jpeg|gif|png|/S*?){1}))$";
        Pattern pattern = Pattern.compile(regular);
        Matcher matcher = pattern.matcher("http://www.foufos.grw");
        System.out.println(matcher.matches());
        long end1 = System.currentTimeMillis();
        System.out.println("regular1 takes "+(end1-start1)+" to match the str");



        long start2 = System.currentTimeMillis();
        System.out.println(domainCheckLegal("wa.foufos.grw.cc"));
        long end2 = System.currentTimeMillis();
        System.out.println("regular2 takes "+(end2-start2)+" to match the str");

        String str = "*.aaa.bbb";
        System.out.println(str.substring(2,str.length()));
    }

    /**
     * 通用匹配
     * @param str
     * @return
     */
    public  static String generalMatch(String str){
        String needCheckStr;
        if (str.startsWith("*")){
            needCheckStr = str.substring(2,str.length());
        } else {
            needCheckStr = str;
        }

        return domainCheckLegal(needCheckStr);
    }

}
