package com.soonsoft.uranus.web.mvc.view;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.soonsoft.uranus.core.common.lang.DateTimeUtils;
import com.soonsoft.uranus.web.constant.HtmlContentType;
import com.soonsoft.uranus.web.json.HtmlJsonFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractView;

/**
 * JsonView
 */
public class JsonView extends AbstractView {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonView.class);

    protected static final String DEFAULT_CONTENT_TYPE = HtmlContentType.Json;

    private Object jsonData;

    private ObjectMapper objectMapper;

    public JsonView() {
        this(null, false);
    }

    public JsonView(Object data, boolean xssProtected) {
        setContentType(DEFAULT_CONTENT_TYPE);
        this.jsonData = data;

        this.objectMapper = xssProtected 
            ? new ObjectMapper(new HtmlJsonFactory())
            : new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtils.ISO8601_FORMAT));
    }

    protected ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ObjectMapper objectMapper = getObjectMapper();
        PrintWriter out = response.getWriter();
        try {
            objectMapper.writeValue(out, this.jsonData);
            response.setContentType(getContentType());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.error("Jackson ObjectMapper Serialize json failed.", e);
        }
    }

}