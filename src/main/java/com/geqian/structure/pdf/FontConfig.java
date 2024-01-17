package com.geqian.structure.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;

public class FontConfig {

    private int fontSize = 10;

    private BaseColor fontColor = BaseColor.BLACK;

    private int fontStyle = Font.NORMAL;

    private String fontFamily;


    public int getFontSize() {
        return fontSize;
    }

    public FontConfig setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public BaseColor getFontColor() {
        return fontColor;
    }

    public FontConfig setFontColor(BaseColor fontColor) {
        this.fontColor = fontColor;
        return this;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public FontConfig setFontStyle(int... fontStyles) {
        for (int fontStyle : fontStyles) {
            this.fontStyle |= fontStyle;
        }
        return this;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public FontConfig setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        return this;
    }
}
