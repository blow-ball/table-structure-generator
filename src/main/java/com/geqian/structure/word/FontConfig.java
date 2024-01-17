package com.geqian.structure.word;

public class FontConfig {
    //文字大小
    private Integer fontSize = 12;
    //文字颜色 16进制格式
    private String fontColor = "000000";
    //文字类型
    private String fontFamily = "Arial";
    //是否斜体
    private boolean fontItalic = false;
    //是否加粗
    private boolean fontBold = false;


    public static FontConfig create(){
        return new FontConfig();
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public FontConfig setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public String getFontColor() {
        return fontColor;
    }

    public FontConfig setFontColor(String fontColor) {
        this.fontColor = fontColor;
        return this;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public FontConfig setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        return this;
    }

    public boolean isFontItalic() {
        return fontItalic;
    }

    public FontConfig setFontItalic(boolean fontItalic) {
        this.fontItalic = fontItalic;
        return this;
    }

    public boolean isFontBold() {
        return fontBold;
    }

    public FontConfig setFontBold(boolean fontBold) {
        this.fontBold = fontBold;
        return this;
    }

    @Override
    public String toString() {
        return "FontConfig{" +
                "fontSize=" + fontSize +
                ", fontColor='" + fontColor + '\'' +
                ", fontFamily='" + fontFamily + '\'' +
                ", fontItalic=" + fontItalic +
                ", fontBold=" + fontBold +
                '}';
    }
}
