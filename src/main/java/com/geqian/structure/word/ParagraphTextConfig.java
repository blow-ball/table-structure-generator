package com.geqian.structure.word;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

public class ParagraphTextConfig {
    //文字大小
    private Integer fontSize = 12;
    //首行缩进字符个数
    private Integer firstLineIndent = 0;
    //文字颜色 16进制格式
    private String color = "000000";
    //文字类型
    private String fontFamily = "Arial";
    //是否加粗
    private boolean blod = false;
    //是否换行
    private boolean carriageReturn = false;
    //是否斜体
    private boolean italic = false;
    //对齐方式
    private ParagraphAlignment alignment = ParagraphAlignment.LEFT;

    public ParagraphTextConfig() {
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public ParagraphTextConfig setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public Integer getFirstLineIndent() {
        return firstLineIndent;
    }

    public ParagraphTextConfig setFirstLineIndent(Integer firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
        return this;
    }

    public String getColor() {
        return color;
    }

    public ParagraphTextConfig setColor(String color) {
        this.color = color;
        return this;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public ParagraphTextConfig setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        return this;
    }

    public boolean isBlod() {
        return blod;
    }

    public ParagraphTextConfig setBlod(boolean blod) {
        this.blod = blod;
        return this;
    }

    public boolean isCarriageReturn() {
        return carriageReturn;
    }

    public ParagraphTextConfig setCarriageReturn(boolean carriageReturn) {
        this.carriageReturn = carriageReturn;
        return this;
    }

    public boolean isItalic() {
        return italic;
    }

    public ParagraphTextConfig setItalic(boolean italic) {
        this.italic = italic;
        return this;
    }

    public ParagraphAlignment getAlignment() {
        return alignment;
    }

    public ParagraphTextConfig setAlignment(ParagraphAlignment alignment) {
        this.alignment = alignment;
        return this;
    }

    @Override
    public String toString() {
        return "ParagraphTextConfig{" +
                "fontSize=" + fontSize +
                ", firstLineIndent=" + firstLineIndent +
                ", color='" + color + '\'' +
                ", fontFamily='" + fontFamily + '\'' +
                ", blod=" + blod +
                ", carriageReturn=" + carriageReturn +
                ", italic=" + italic +
                ", alignment=" + alignment +
                '}';
    }
}