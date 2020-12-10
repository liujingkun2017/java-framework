package org.liujk.java.framework.boot.starter.web.common;

import com.google.common.collect.Lists;
import org.liujk.java.framework.boot.starter.web.config.WebResponseProperties;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class ResponseHeaderFilter extends OncePerRequestFilter implements Ordered {

    private int order = Ordered.LOWEST_PRECEDENCE;

    private WebResponseProperties webResponseProperties;

    private String cacheString = null;

    private int cacheMaxAge = 0;

    public ResponseHeaderFilter(WebResponseProperties webResponseProperties, int cacheMaxAge) {
        this.webResponseProperties = webResponseProperties;
        this.cacheString = "max-age=" + cacheMaxAge;
        this.cacheMaxAge = cacheMaxAge;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (webResponseProperties != null) {
            for (Map.Entry<String, String> entry : webResponseProperties.getHeaders().entrySet()) {
                response.setHeader(entry.getKey(), entry.getValue());
            }
        }
        if (cacheMaxAge != -1) {
            String uri = request.getRequestURI();
            if (uri != null) {
                if (uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".png") || uri.endsWith(".jpg")
                        || uri.endsWith(".gif")) {
                    if (!response.containsHeader("Cache-Control")) {
                        response.setHeader("Cache-Control", cacheString);
                    }
                }
            }
        }

        filterChain.doFilter(request, response);

        // 对header进行编码，解决中文乱码问题
        List<String> headerNames = Lists.newArrayList(response.getHeaderNames());
        for (String header : headerNames) {
            String value = response.getHeader(header);
            response.setHeader(header, URLEncoder.encode(value, "UTF-8"));
        }

        return;

    }

    public void setOrder(int order) {
        this.order = order;
    }
}
