package com.geqian.structure.utils;

import com.documents4j.api.DocumentType;
import com.documents4j.job.LocalConverter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Objects;

/**
 * @author geqian
 * @date 15:40 2023/7/9
 */
@Slf4j
public class WordToPdfUtils {
    /**
     * 通过documents4j 实现word转pdf
     *
     * @param sourcePath
     * @param targetPath
     */
    public static void word2007ToPdf(String sourcePath, String targetPath) {
        File inputWord = new File(sourcePath);
        File outputFile = new File(targetPath);
        LocalConverter converter = null;
        try (InputStream inputStream = new FileInputStream(inputWord);
             OutputStream outputStream = new FileOutputStream(outputFile)) {

            converter = (LocalConverter) LocalConverter.builder().build();
            converter.convert(inputStream)
                    .as(DocumentType.DOCX)
                    .to(outputStream)
                    .as(DocumentType.PDF).execute();
        } catch (Exception e) {
            log.error("word转pdf失败:{}", e.toString());
        } finally {
            if (Objects.nonNull(converter)) {
                converter.shutDown();
            }
        }
    }


    /**
     * 通过documents4j 实现word转pdf
     *
     * @param wordBytes
     * @return
     */
    @SneakyThrows
    public static byte[] word2007ToPdf(byte[] wordBytes) {
        LocalConverter converter = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(wordBytes)) {

            converter = (LocalConverter) LocalConverter.builder().build();
            converter.convert(inputStream)
                    .as(DocumentType.DOCX)
                    .to(outputStream)
                    .as(DocumentType.PDF).execute();
        } catch (Exception e) {
            log.error("word转pdf失败:{}", e.toString());
        } finally {
            if (Objects.nonNull(converter)) {
                converter.shutDown();
            }
            outputStream.close();
        }
        return outputStream.toByteArray();
    }


    /**
     * 通过documents4j 实现word转pdf
     *
     * @param sourcePath
     * @param targetPath
     */
    public static void word2003ToPdf(String sourcePath, String targetPath) {
        File inputWord = new File(sourcePath);
        File outputFile = new File(targetPath);
        LocalConverter converter = null;
        try (InputStream inputStream = new FileInputStream(inputWord);
             OutputStream outputStream = new FileOutputStream(outputFile)) {

            converter = (LocalConverter) LocalConverter.builder().build();
            converter.convert(inputStream)
                    .as(DocumentType.DOC)
                    .to(outputStream)
                    .as(DocumentType.PDF).execute();
        } catch (Exception e) {
            log.error("word转pdf失败:{}", e.toString());
        } finally {
            if (Objects.nonNull(converter)) {
                converter.shutDown();
            }
        }
    }

    /**
     * 通过documents4j 实现word转pdf
     *
     * @param wordBytes
     * @return
     */
    @SneakyThrows
    public static byte[] word2003ToPdf(byte[] wordBytes) {
        LocalConverter converter = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(wordBytes)) {

             converter = (LocalConverter) LocalConverter.builder().build();
            converter.convert(inputStream)
                    .as(DocumentType.DOC)
                    .to(outputStream)
                    .as(DocumentType.PDF).execute();
        } catch (Exception e) {
            log.error("word转pdf失败:{}", e.toString());
        } finally {
            if (Objects.nonNull(converter)){
                converter.shutDown();
            }
            outputStream.close();
        }
        return outputStream.toByteArray();
    }


}
