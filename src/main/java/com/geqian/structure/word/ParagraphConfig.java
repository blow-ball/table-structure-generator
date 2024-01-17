package com.geqian.structure.word;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

public class ParagraphConfig {
    //首行缩进字符个数
    private Integer firstLineIndent = 0;
    //是否换行
    private boolean carriageReturn = false;
    //对齐方式
    private ParagraphAlignment alignment = ParagraphAlignment.LEFT;

    private FontConfig fontConfig = FontConfig.create();


    private ParagraphConfig() {
    }

    public static ParagraphConfig create() {
        return new ParagraphConfig();
    }

    public Integer getFirstLineIndent() {
        return firstLineIndent;
    }

    public ParagraphConfig setFirstLineIndent(Integer firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
        return this;
    }

    public boolean isCarriageReturn() {
        return carriageReturn;
    }

    public ParagraphConfig setCarriageReturn(boolean carriageReturn) {
        this.carriageReturn = carriageReturn;
        return this;
    }

    public ParagraphAlignment getAlignment() {
        return alignment;
    }

    public ParagraphConfig setAlignment(ParagraphAlignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public FontConfig getFontConfig() {
        return fontConfig;
    }

    public ParagraphConfig setFontConfig(FontConfig fontConfig) {
        this.fontConfig = fontConfig;
        return this;
    }

    @Override
    public String toString() {
        return "ParagraphConfig{" +
                "firstLineIndent=" + firstLineIndent +
                ", carriageReturn=" + carriageReturn +
                ", alignment=" + alignment +
                ", fontConfig=" + fontConfig +
                '}';
    }
}