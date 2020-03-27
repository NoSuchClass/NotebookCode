package com.bitongchong.notebook.note6.tomcat;

import com.bitongchong.notebook.note6.tomcat.http.AbstractBitServlet;
import com.bitongchong.notebook.note6.tomcat.http.BitRequest;
import com.bitongchong.notebook.note6.tomcat.http.BitResponse;
import com.bitongchong.notebook.util.ThreadPoolsUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author liuyuehe
 * @date 2020/3/27 9:46
 */
public class BitTomcat {
    /**
     * J2EE标准
     * Servlet
     * Request
     * Response
     * <p>
     * 主流程：
     * <p>
     * 1、配置端口，创建ServerSocket
     * 2、配置web.xml，写一个Servlet，继承HttpServlet
     * servlet-name
     * servlet-class
     * url-pattern
     * 3、读取配置，同时建立url-pattern和Servlet之间的映射关系
     * Map servletMapping
     * 4、HTTP请求，它发送的数据实际上就是一堆符合HTTP协议规范的字符串
     * 5、解析一下协议内容，拿到URL，反射实例化对应的Servlet
     * 6、调用对应实例的service方法，执行具体逻辑doGet/doPost
     * 7、其中有两个重要的对象：Request（InputStream）/Response（OutputStream）
     */
    private int port = 8080;
    private ServerSocket server;
    private Map<String, AbstractBitServlet> servletMap = new HashMap<>();
    private Properties properties = new Properties();

    private void init() {
        try {
            String webResourcePath = this.getClass().getResource("/").getPath();
            FileInputStream fileInputStream = new FileInputStream(webResourcePath + "my-tomcat.properties");
            properties.load(fileInputStream);
            for (Object keyObj : properties.keySet()) {
                String key = String.valueOf(keyObj);
                if (key.endsWith(".url")) {
                    String url = properties.getProperty(key);
                    String servletProperties = key.replaceAll(".url", "");
                    servletProperties = servletProperties + ".class";
                    String className = properties.getProperty(servletProperties);
                    AbstractBitServlet servlet = (AbstractBitServlet) Class.forName(className).newInstance();
                    servletMap.put(url, servlet);
                }
            }
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        init();

        try {
            server = new ServerSocket(port);
            System.out.println("Bit Tomcat started at :" + port);
            while (true) {
                Socket client = server.accept();
                ThreadPoolsUtil.getInstance().submitTask(() -> {
                    try {
                        process(client);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(Socket client) throws IOException {
        InputStream inputStream = client.getInputStream();
        OutputStream outputStream = client.getOutputStream();
        BitRequest request = new BitRequest(inputStream);
        BitResponse response = new BitResponse(outputStream);
        String url = request.getUrl();
        if (servletMap.containsKey(url)) {
            servletMap.get(url).service(request, response);
        } else {
            response.write("404 not found!");
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        client.close();
    }

    public static void main(String[] args){
        BitTomcat tomcat = new BitTomcat();
        tomcat.start();
    }
}
