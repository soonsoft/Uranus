package com.soonsoft.uranus.web.mvc.controller;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.web.HttpContext;
import com.soonsoft.uranus.web.mvc.view.ContentView;
import com.soonsoft.uranus.web.mvc.view.JsonView;
import com.soonsoft.uranus.web.mvc.view.file.FileDataView;
import com.soonsoft.uranus.web.mvc.view.file.FileStreamView;
import com.soonsoft.uranus.web.mvc.view.file.FileView;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.HtmlUtils;

public abstract class AbstractController {

    public ModelAndView view(String viewName) {
        return new ModelAndView(viewName);
    }

    public ModelAndView view(String viewName, Object modelObject) {
        return new ModelAndView(viewName, "ViewModel", modelObject);
    }

    public ModelAndView view(String viewName, Map<String, Object> model) {
        return new ModelAndView(viewName, model);
    }

    public FileView file(byte[] fileContents) {
        FileDataView fileView = new FileDataView(fileContents);
        return fileView;
    }

    public FileView file(byte[] fileContents, String contentType) {
        FileDataView fileView = new FileDataView(fileContents, contentType);
        return fileView;
    }

    public FileView file(InputStream fileStream) {
        FileStreamView fileView = new FileStreamView(fileStream);
        return fileView;
    }

    public FileView file(InputStream fileStream, String contentType) {
        FileStreamView fileView = new FileStreamView(fileStream, contentType);
        return fileView;
    }

    public JsonView json(Object jsonData) {
        return new JsonView(jsonData, false);
    }

    public JsonView jsonXssProtected(Object jsonData) {
        return new JsonView(jsonData, true);
    }

    public ContentView content(String content) {
        return new ContentView(content);
    }

    public ContentView content(String content, String contentType) {
        return new ContentView(content, contentType);
    }

    public RedirectView redirect(String url) {
        return new RedirectView(url);
    }
    
    public HttpServletRequest getRequest() {
        return HttpContext.current().getRequest();
    }

    public HttpServletResponse getResponse() {
        return HttpContext.current().getResponse();
    }

    public HttpSession getSession() {
        return HttpContext.current().getSession();
    }

    public String htmlEncode(String input) {
        if(StringUtils.isEmpty(input)) {
            return input;
        }
        return HtmlUtils.htmlEscape(input);
    }

    public String htmlDecode(String input) {
        if(StringUtils.isEmpty(input)) {
            return input;
        }
        return HtmlUtils.htmlUnescape(input);
    }
    
}