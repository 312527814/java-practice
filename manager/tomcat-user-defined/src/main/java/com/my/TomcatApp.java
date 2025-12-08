package com.my;

import com.my.servlet.IndexServlet;
import com.my.servlet.IndexServlet2;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Bootstrap;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.TaskThreadFactory;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.concurrent.*;

/**
 * @program:
 * @description:
 * @author: liang.liu
 * @create: 2021-07-22 10:21
 */
public class TomcatApp {
    private static int PORT = 8080;
    private static String CONTEX_PATH = "/demosevlet";
    private static String SERVLET_NAME = "index";
    private static String MAPPING = "index";

    public static void main(String[] args) throws LifecycleException, InterruptedException {
        System.out.println("开始启动tomcat");


        Tomcat tomcatServer = new Tomcat();
        //设置tomcat端口
        tomcatServer.setPort(PORT);
        //此tomcat端口是否自动部署
        tomcatServer.getHost().setAutoDeploy(false);
        //创建一个web应用程序
        StandardContext standardContex = new StandardContext();

        //设置web应用程序的上下文地址
        standardContex.setPath(CONTEX_PATH);
        //添加web应用程序的监听
        standardContex.addLifecycleListener(new Tomcat.FixContextListener());
        //将web应用程序添加到服务器中
        tomcatServer.getHost().addChild(standardContex);
        //配置servelt和映射

//        tomcatServer.addServlet(CONTEX_PATH, SERVLET_NAME, new IndexServlet());

        tomcatServer.addServlet(CONTEX_PATH, SERVLET_NAME, dispatcherServlet());

        tomcatServer.addServlet(CONTEX_PATH, SERVLET_NAME+"1", new IndexServlet2());
        standardContex.addServletMappingDecoded("/" + MAPPING, SERVLET_NAME);

        standardContex.addServletMappingDecoded("/" + MAPPING+"1", SERVLET_NAME+"1");
        Connector connector = tomcatServer.getConnector();
        {
            //设置最大线程数
            ProtocolHandler protocolHandler = connector.getProtocolHandler();
            TaskQueue taskqueue = new TaskQueue();
            TaskThreadFactory tf = new TaskThreadFactory("my thead-exec-", true, 5);
            ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 20, TimeUnit.SECONDS, taskqueue, tf);
            protocolHandler.setExecutor(executor);
            taskqueue.setParent(executor);

        }

        {
            //设置最大线程数
//            ProtocolHandler protocolHandler = connector.getProtocolHandler();
//
//            AbstractProtocol protocol = (AbstractProtocol) protocolHandler;
//            protocol.setMaxThreads(20);
        }
        //启动tomcat
        tomcatServer.start();
        System.out.println("启动tomcat完毕");
        //开启异步服务，接收请求
        tomcatServer.getServer().await();
    }

    public static DispatcherServlet dispatcherServlet() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setDispatchOptionsRequest(true);
        dispatcherServlet.setDispatchTraceRequest(false);
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(false);
        dispatcherServlet.setPublishEvents(true);
        dispatcherServlet.setEnableLoggingRequestDetails(false);
        return dispatcherServlet;
    }
}
