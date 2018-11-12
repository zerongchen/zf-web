package com.aotain.zongfen.utils;

import org.apache.hadoop.classification.InterfaceAudience;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPUtil {

    private static final Logger LOG = LoggerFactory.getLogger(IPUtil.class);
    /** 
     * ip地址转成long型数字 
     * 将IP地址转化成整数的方法如下： 
     * 1、通过String的split方法按.分隔得到4个长度的数组 
     * 2、通过左移位操作（<<）给每一段的数字加权，第一段的权为2的24次方，第二段的权为2的16次方，第三段的权为2的8次方，最后一段的权为1 
     * @param strIp 
     * @return 
     */  
    public static long ipToLong(String strIp) {  
        String[]ip = strIp.split("\\.");  
        return (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16) + (Long.parseLong(ip[2]) << 8) + Long.parseLong(ip[3]);  
    }  
  
    /** 
     * 将十进制整数形式转换成127.0.0.1形式的ip地址 
     * 将整数形式的IP地址转化成字符串的方法如下： 
     * 1、将整数值进行右移位操作（>>>），右移24位，右移时高位补0，得到的数字即为第一段IP。 
     * 2、通过与操作符（&）将整数值的高8位设为0，再右移16位，得到的数字即为第二段IP。 
     * 3、通过与操作符吧整数值的高16位设为0，再右移8位，得到的数字即为第三段IP。 
     * 4、通过与操作符吧整数值的高24位设为0，得到的数字即为第四段IP。 
     * @param longIp 
     * @return 
     */  
    public static String longToIP(long longIp) {  
        StringBuffer sb = new StringBuffer("");  
        // 直接右移24位  
        sb.append(String.valueOf((longIp >>> 24)));  
        sb.append(".");  
        // 将高8位置0，然后右移16位  
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));  
        sb.append(".");  
        // 将高16位置0，然后右移8位  
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));  
        sb.append(".");  
        // 将高24位置0  
        sb.append(String.valueOf((longIp & 0x000000FF)));  
        return sb.toString();  
    }

    /**
     * 将字符串形式的ip地址转换为long
     *
     * @param ipInString
     *            字符串形式的ip地址
     * @return long形式的ip地址
     */
    public static long StringToLong(String ipInString) {

        ipInString = ipInString.replace(" ", "");
        byte[] bytes;
        if (ipInString.contains(":"))
            bytes = ipv6ToBytes(ipInString);
        else
            bytes = ipv4ToBytes(ipInString);
        BigInteger bigInt = new BigInteger(bytes);
        return bigInt.longValue();
    }

    /**
     * ipv4地址转有符号byte[5]
     */
    private static byte[] ipv4ToBytes(String ipv4) {
        byte[] ret = new byte[5];
        ret[0] = 0;
        // 先找到IP地址字符串中.的位置
        int position1 = ipv4.indexOf(".");
        int position2 = ipv4.indexOf(".", position1 + 1);
        int position3 = ipv4.indexOf(".", position2 + 1);
        // 将每个.之间的字符串转换成整型
        ret[1] = (byte) Integer.parseInt(ipv4.substring(0, position1));
        ret[2] = (byte) Integer.parseInt(ipv4.substring(position1 + 1,
                position2));
        ret[3] = (byte) Integer.parseInt(ipv4.substring(position2 + 1,
                position3));
        ret[4] = (byte) Integer.parseInt(ipv4.substring(position3 + 1));
        return ret;
    }

    /**
     * ipv6地址转有符号byte[17]
     */
    private static byte[] ipv6ToBytes(String ipv6) {
        byte[] ret = new byte[17];
        ret[0] = 0;
        int ib = 16;
        boolean comFlag = false;// ipv4混合模式标记
        if (ipv6.startsWith(":"))// 去掉开头的冒号
            ipv6 = ipv6.substring(1);
        String groups[] = ipv6.split(":");
        for (int ig = groups.length - 1; ig > -1; ig--) {// 反向扫描
            if (groups[ig].contains(".")) {
                // 出现ipv4混合模式
                byte[] temp = ipv4ToBytes(groups[ig]);
                ret[ib--] = temp[4];
                ret[ib--] = temp[3];
                ret[ib--] = temp[2];
                ret[ib--] = temp[1];
                comFlag = true;
            } else if ("".equals(groups[ig])) {
                // 出现零长度压缩,计算缺少的组数
                int zlg = 9 - (groups.length + (comFlag ? 1 : 0));
                while (zlg-- > 0) {// 将这些组置0
                    ret[ib--] = 0;
                    ret[ib--] = 0;
                }
            } else {
                int temp = Integer.parseInt(groups[ig], 16);
                ret[ib--] = (byte) temp;
                ret[ib--] = (byte) (temp >> 8);
            }
        }
        return ret;
    }

    public static void main(String[] args) {
        String ipV6="fe80:0:0:0:d48a:e3be:7d9d:ffff";
        byte[] bytes =IPUtil.ipv6ToBytes(ipV6);
        BigInteger bigInt = new BigInteger(bytes);
        System.out.println(bigInt.longValue());
    }
    /**
     * 检查两个ip是否有包含和相同
     * @return
     */
    public static boolean checkTwoIp(Long sIp1,Long eIp1,Long sIp2,Long eIp2){
        if(sIp2>=eIp1||sIp1>=eIp2){
            return false;
        }
        return true;
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getIpByLine(String line){
        String ip = null;
        try {
            ip = null;
            String regEx = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(line);

            if (m.find()) {
                String result = m.group();
                ip = result;
            }
        } catch (Exception e) {
            LOG.error(" ",e);
        }
        return ip;
    }

}
