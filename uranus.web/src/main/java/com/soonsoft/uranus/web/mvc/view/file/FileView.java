package com.soonsoft.uranus.web.mvc.view.file;

import com.soonsoft.uranus.web.constant.HtmlContentType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractView;

/**
 * FileView
 */
public abstract class FileView extends AbstractView {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileView.class);

    protected static final String DEFAULT_CONTENT_TYPE = HtmlContentType.File.File;

    public FileView() {
        setContentType(DEFAULT_CONTENT_TYPE);
    }

    public FileView(String contentType) {
        setContentType(contentType);
    }

}