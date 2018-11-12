package com.aotain.zongfen.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 比较两个list集合元素是否相等
 *
 * @author daiyh@aotain.com
 * @date 2018/04/17
 */
public class CollectionUtils {

    private static final Integer INTEGER_ONE = 1;

    public static boolean isEqualCollection(Collection a, Collection b){
        // 长度不一致，返回false
        if (a.size()!=b.size()){
            return false;
        }

        Map mapa = getCardinalityMap(a);
        Map mapb = getCardinalityMap(b);

        // 再次比较map长度
        if (mapa.size()!=mapb.size()){
            return false;
        }
        // 比较map的key-value看每个元素以及每个元素的个数是否一致
        Iterator iterator = mapa.keySet().iterator();
        while (iterator.hasNext()){
            Object obj = iterator.next();
            // 获取obj对应的次数
            if (getFreq(obj,mapa)!=getFreq(obj,mapb)){
                return false;
            }
        }

        return true;
    }

    /**
     * 以obj为key，可以防止重复，如果重复就value++
     * 这样实际上记录了元素以及出现的次数
     */
    public static Map getCardinalityMap(Collection coll) {
        Map count = new HashMap();
        for (Iterator it = coll.iterator(); it.hasNext();) {
            Object obj = it.next();
            Integer c =(Integer) count.get(obj);
            if (c == null){
                count.put(obj, INTEGER_ONE);
            } else {
                count.put(obj, new Integer(c.intValue() + 1));
            }
        }
        return count;
    }

    /**
     * 获取obj在map中的个数
     * @param obj
     * @param freqMap
     */
    private static final int getFreq(Object obj, Map freqMap){
        Integer count = (Integer) freqMap.get(obj);
        if (count!=null){
            return count.intValue();
        }
        return 0;
    }
}
