package com.soonsoft.uranus.web.constant;

/**
 * constant of html content type
 */
public interface HtmlContentType {

    static final String Html = "text/html";

    static final String Text = "text/plain";

    static final String Json = "application/json";

    static final String Javascript = "application/javascript";

    static final String CSS = "text/css";

    static final String XML = "text/xml";

    interface File {

        static final String File = "application/octet-stream";

        static final String PDF = "application/pdf";

        static final String DOC = "application/msword";

        static final String DOCX = "application/msword";

        static final String XLS = "application/vnd.ms-excel";

        static final String XLSX = "application/vnd.ms-excel";

        static final String PPT = "application/vnd.ms-powerpoint";

        static final String PPTX = "application/vnd.ms-powerpoint";
    }

    interface Image {

        static final String PNG = "image/png";

        static final String JPG = "image/jpeg";

        static final String GIF = "image/gif";

        static final String ICO = "image/x-icon";
    }

    interface Audio {

        static final String MP3 = "audio/mp3";

        static final String AAC = "audio/m4a";
    }

    interface Video {

        static final String MP4 = "video/mpeg4";

        static final String AVI = "video/avi";

        static final String MKV = "video/mkv";
    }
}