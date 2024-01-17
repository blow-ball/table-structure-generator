package com.geqian.structure.pdf;

import com.itextpdf.text.Element;

/**
 * @author geqian
 * @date 16:14 2024/1/16
 */

public class ParagraphConfig {


    private float firstLineIndent = 0;
    private int alignment = Element.ALIGN_LEFT;
    private FontConfig fontConfig = new FontConfig();


    private ParagraphConfig() {

    }

    /**
     * 创建配置对象
     *
     * @return
     */
    public static ParagraphConfig create() {
        return new ParagraphConfig();
    }

    public float getFirstLineIndent() {
        return firstLineIndent;
    }

    public ParagraphConfig setFirstLineIndent(float firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
        return this;
    }

    public int getAlignment() {
        return alignment;
    }

    public ParagraphConfig setAlignment(int alignment) {
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


}
