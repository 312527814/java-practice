//package com.my.filter;
//
//
//import com.my.log.LoggerInfo;
//import org.apache.kafka.clients.producer.*;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequestWrapper;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Properties;
//import java.util.concurrent.Future;
//
///**
// * @program:
// * @description:
// * @author: liang.liu
// * @create: 2021-10-12 16:03
// */
////@Component
//public class MyTracingFilter2 implements Filter {
//    @Value("${spring.application.name}")
//    private String application;
//    @Autowired
//    Tracing tracing;
//    @Autowired
//    SpanNamer spanNamer;
//
//
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
//
//        run((HttpServletRequest)servletRequest,(HttpServletResponse)servletResponse);
//        // 将新request包装对象传入过滤器链中
//        chain.doFilter(servletRequest, servletResponse);
//
//    }
//
//    @Autowired
//    HttpTracing httpTracing;
//    public Object run(HttpServletRequest req, HttpServletResponse resp) {
//        Object span = req.getAttribute(SpanCustomizer.class.getName());
////        SpanCustomizer customizer=(SpanCustomizer)span;
////        customizer.tag("mvc.controller.class", "handlerMethod.getBeanType().getSimpleName()");
////        customizer.tag("mvc.controller.method", "handlerMethod.getMethod().getName()");
//
//        HttpServerResponse request = new HttpServerResponse2(req, resp);
//
//        Throwable exception = null;
//        Span currentSpan = this.tracing.tracer().currentSpan();
//
//        HttpServerHandler<HttpServerRequest, HttpServerResponse> handler = HttpServerHandler.create(httpTracing);
//        handler.handleSend(request, exception, currentSpan);
//        return null;
//    }
//
//    static final class HttpServerResponse2 extends brave.http.HttpServerResponse {
//
//        final HttpServletResponse delegate;
//
//        final String method;
//
//        final String httpRoute;
//
//        HttpServerResponse2(HttpServletRequest req, HttpServletResponse resp) {
//            this.delegate = resp;
//            this.method = req.getMethod();
//            this.httpRoute = (String) req.getAttribute("http.route");
//        }
//
//        @Override
//        public String method() {
//            return method;
//        }
//
//        @Override
//        public String route() {
//            return httpRoute;
//        }
//
//        @Override
//        public HttpServletResponse unwrap() {
//            return delegate;
//        }
//
//        @Override
//        public int statusCode() {
//            return delegate.getStatus();
//        }
//
//    }
//
//}
