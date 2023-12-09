package com.geqian.structure.utils;

import java.util.UUID;

/**
 * @author geqian
 * @date 22:50 2022/8/30
 */
public class UUIDUtils {

    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

}
