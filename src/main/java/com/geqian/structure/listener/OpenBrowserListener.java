package com.geqian.structure.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Administrator
 */
@Component
@ConditionalOnProperty(name = "spring.application.start.openBrowser", havingValue = "true")
public class OpenBrowserListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${spring.application.start.access-url}")
    private String accessUrl;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 在Spring Boot应用启动后自动打开浏览器访问8080端口
        openBrowser(accessUrl);
    }

    private void openBrowser(String url) {
        try {
            //Desktop.isDesktopSupported()判断操作系统是否支持Desktop类，如果支持，则使用Desktop.browse方法直接打开浏览器访问URL；
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                //如果不支持，Runtime.getRuntime().exec("cmd /c start " + url)用于执行cmd /c start命令来打开默认浏览器访问指定的URL
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("cmd /c start " + url);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
