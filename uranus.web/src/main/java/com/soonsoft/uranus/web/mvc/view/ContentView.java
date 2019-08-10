package com.soonsoft.uranus.web.mvc.view;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.web.constant.HtmlContentType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractView;

/**
 * ContentView
 */
public class ContentView extends AbstractView {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentView.class);

    protected static final String DEFUALT_CONTENT_TYPE = HtmlContentType.Text;

    private String content;

    public ContentView(String content) {
        setContentType(DEFUALT_CONTENT_TYPE);
        this.content = content;
    }

    public ContentView(String content, String contentType) {
        setContentType(contentType);
        this.content = content;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        response.setContentType(getContentType());

        PrintWriter out = response.getWriter();
        try {
            out.print(content);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.error("write content to reponse occur failed.", e);
        }
    }

    
}