package com.geqian.structure.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

public class ConsoleLogCharsetProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        String charsetName = getSystemDefaultCharsetName();
        //添加一个配置
        Properties properties = new Properties();
        //设置控制台日志输出字符编码
        properties.put("logging.charset.console", charsetName);

        PropertiesPropertySource extendPropertySource = new PropertiesPropertySource("application-extend.properties", properties);
        MutablePropertySources propertySources = environment.getPropertySources();
        /**
         * get 获取属性源
         * addFirst 添加为第一个属性源
         * addLast 设置为最后一个属性源
         * addBefore 在某个属性源之前添加一个属性源
         * addAfter 在某个属性源之后添加一个属性源
         * replace 替换某个属性源
         * replace 删除某个属性源
         */
        propertySources.addLast(extendPropertySource);
    }


    /**
     * 获取字符编码名称
     *
     * @return
     */
    private String getSystemDefaultCharsetName() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? "GBK" : "UTF-8";
    }

}