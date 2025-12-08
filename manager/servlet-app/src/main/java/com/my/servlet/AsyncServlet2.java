package com.my.servlet;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @program:
 * @description:
 * @author: liang.liu
 * @create: 2021-09-03 18:56
 */
@WebServlet(name="AsyncServlet2",urlPatterns="/AsyncServlet2",asyncSupported = true)
public class AsyncServlet2 extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("threadname "+Thread.currentThread().getName());
        AsyncContext asyncContext = req.startAsync();
        asyncContext.setTimeout(1000*60);
//        asyncContext.start(()->{
//
//        });
        try {
            PrintWriter writer = null;
//            Thread.sleep(1000*3);
            writer = resp.getWriter();
            writer.write("asyncContext...");
            writer.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
        PrintWriter writer = resp.getWriter();
        writer.write("hello...");
        writer.flush();


    }
}
