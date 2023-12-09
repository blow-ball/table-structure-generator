package com.geqian.structure.word;

/**
 * @author geqian
 * @date 19:58 2023/11/8
 */
public class NoConverter implements Converter {

    @Override
    public Object convert(Object fieldValue) {
        return fieldValue;
    }
}
