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

    @Value("${server.port}")
    private String port;
    @Value("${spring.application.start.access-ip}")
    private String accessIp;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 在Spring Boot应用启动后自动打开浏览器访问8080端口
        openBrowser(accessIp+port);
    }

    private void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                // 如果支持Desktop类，则使用它来打开URL
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // 非Windows环境的处理
                String os = System.getProperty("os.name").toLowerCase();
                Runtime runtime = Runtime.getRuntime();
                if (os.contains("win")) {
                    // Windows环境
                    runtime.exec("cmd /c start " + url);
                } else if (os.contains("mac")) {
                    // Mac环境
                    runtime.exec("open " + url);
                } else if (os.contains("nix") || os.contains("nux")) {
                    // Unix/Linux环境
                    runtime.exec("xdg-open " + url);
                } else {
                    // 其他情况，输出无法打开浏览器的信息
                    System.out.println("无法打开浏览器，请手动访问: " + url);
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
