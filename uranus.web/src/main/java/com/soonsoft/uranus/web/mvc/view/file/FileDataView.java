package com.soonsoft.uranus.web.mvc.view.file;

import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FileDataView
 */
public class FileDataView extends FileView {

    private byte[] fileData;

    public FileDataView(byte[] fileData) {
        super();
        this.fileData = fileData;
    }

    public FileDataView(byte[] fileData, String contentType) {
        super(contentType);
        this.fileData = fileData;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        response.setContentType(getContentType());
        if(fileData != null && fileData.length > 0) {
            try (ServletOutputStream output = response.getOutputStream()) {
                output.write(fileData);
                output.flush();
            } catch(Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                LOGGER.error("output File occur error.", e);
            }
        }
    }
}