package com.soonsoft.uranus.web.mvc.view.file;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class FileStreamView extends FileView {

    private InputStream fileStream;

    public FileStreamView(InputStream fileStream) {
        super();
        this.fileStream = fileStream;
    }

    public FileStreamView(InputStream fileStream, String contentType) {
        super(contentType);
        this.fileStream = fileStream;
    }

    @Override
    protected void renderMergedOutputModel(
        @Nullable Map<String, Object> model, 
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response) throws Exception {

        response.setContentType(getContentType());
        if(fileStream != null) {
            try (ServletOutputStream output = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int len;
                while((len = fileStream.read(buffer)) > 0) {
                    output.write(buffer, 0, len);
                }
                output.flush();
            } catch(Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                LOGGER.error("output File occur error.", e);
            }
        }
        
    }

    
}