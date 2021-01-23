package com.soonsoft.uranus.security.config.properties;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;

import org.springframework.beans.factory.annotation.Value;

public class SecurityProperties {

    private List<String> resourcePathList;

    public List<String> getResourcePathList() {
        return resourcePathList;
    }

    public String[] getResourcePathArray() {
        if(CollectionUtils.isEmpty(resourcePathList)) {
            return new String[0];
        }

        String[] arr = new String[resourcePathList.size()];
        return resourcePathList.toArray(arr);
    }

    public void setResourcePathList(List<String> resourcePathList) {
        this.resourcePathList = resourcePathList;
    }

    @Value("${uranus-web.security.resource-path:/style/**,/content/**,/script/**,/favicon.ico}")
    public void initResourcePathList(String resourcePath) {
        if(StringUtils.isBlank(resourcePath)) {
            resourcePathList = new ArrayList<>(0);
            return;
        }

        String[] arr = resourcePath.split(",");
        ArrayList<String> pathList = new ArrayList<>(arr.length);

        for(String path : arr) {
            if(!StringUtils.isBlank(path)) {
                pathList.add(path);
            }
        }

        resourcePathList = pathList;
    }

    @Value("${uranus-web.security.sessionid-header:X-AUTH-URANUS-SID}")
    private String sessionIdHeaderName;

    public String getSessionIdHeaderName() {
        return sessionIdHeaderName;
    }

    public void setSessionIdHeaderName(String sessionIdHeaderName) {
        this.sessionIdHeaderName = sessionIdHeaderName;
    }
    
}
