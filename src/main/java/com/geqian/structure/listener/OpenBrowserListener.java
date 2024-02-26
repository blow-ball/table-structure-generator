package com.geqian.structure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

/**
 * @author Administrator
 */
@Component
@ConditionalOnProperty(prefix = "spring.application.start.openBrowser", name = "enable", havingValue = "true")
public class OpenBrowserListener implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger log = LoggerFactory.getLogger(OpenBrowserListener.class);

    @Value("${server.port}")
    private String port;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${spring.application.start.openBrowser.access-url:}")
    private String accessUrl;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            String url = accessUrl.isEmpty()
                    ? "http://" + getNetworkIp() + ":" + port + contextPath
                    : accessUrl;
            // 在 Spring Boot 应用启动后自动打开浏览器访问 url
            openBrowser(url);
        } catch (UnknownHostException e) {
            log.error("获取服务网络ip地址时发生异常", e);
        }
    }

    /**
     * 项目启动自动打开浏览器访问 url
     *
     * @param url
     */
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
                    log.error("无法打开浏览器，请手动访问:{} ", url);
                }
            }
        } catch (IOException | URISyntaxException e) {
            // 其他情况，输出无法打开浏览器的信息
            log.error("项目启动打开浏览器发生异常", e);
        }
    }


    /**
     * 网络 ip
     *
     * @return
     * @throws UnknownHostException
     */
    private String getNetworkIp() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        if (inetAddress.isLoopbackAddress()) {
            // 如果本地IP是环回地址，则获取第一个非环回地址
            InetAddress[] allAddresses = InetAddress.getAllByName(inetAddress.getHostName());
            for (InetAddress address : allAddresses) {
                if (!address.isLoopbackAddress()) {
                    return address.getHostAddress();
                }
            }
        }
        return inetAddress.getHostAddress();
    }

}
