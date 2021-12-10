package com.soonsoft.uranus.util.file;

import com.soonsoft.uranus.core.common.lang.StringUtils;

// 文件类型清单：https://www.cnblogs.com/senior-engineer/p/9541719.html
public enum FileType {

    // 图片
    JPEG("FFD8FF", "jpg", new String[] { "jpeg", "jpe", "jfif", "jif", "jfi" }),
    PNG("89504E47", "png"),
    GIF("47494638", "gif"),
    BMP("424D", "bmp"),
    TIFF("49492A00", "tif", new String[] { "tiff" }),
    // 文本
    HTML("68746D6C3E", "html", new String[] { "htm" }),
    XML("3C3F786D6C", "xml"),
    // 文档
    PDF("255044462D312E", "pdf"),
    PSD("38425053", "psd"),
    DWG("41433130", "dwg"),
    // 文件
    RAR("52617221", "rar"),
    ZIP("504B0304", "zip"),
    JAR("5F27A889", "jar"),
    ISO("4344303031", "iso"),
    EXE("4D5A", "exe", new String[] { "dll", "drv", "vxd", "sys", "ocx", "vbx" }),
    // 媒体文件
    M4A("00000020667479704D34412000000000", "m4a", new String[] { "m4v" }),
    MKV("1A45DFA3934282886D6174726F736B61", "mkv"),
    MP3("494433", "mp3"),
    MP4("000000186674797033677035", "mp4"),
    WMA("3026B2", "wma", new String[] { "wmv" }),
    FLV("464C5601", "flv"),
    MOV("6D6F6F76", "mov"),
    MAV("57415645", "mav"),
    AVI("41564920", "avi"),
    RMVB("2E524D46", "rmvb", new String[] { "rm" }),
    // 其他
    UNKNOWN(StringUtils.Empty, StringUtils.Empty)
    ;
 

    private String magicHeader;
    private String extensionName;
    private String[] alias;

    private FileType(String magicHeader, String extensionName) {
        this(magicHeader, extensionName, null);
    }

    private FileType(String magicHeader, String extensionName, String[] alias) {
        this.magicHeader = magicHeader;
        this.extensionName = extensionName;
    }

    public boolean checkExtensionName(String extname) {
        if(StringUtils.isBlank(extname)) {
            return false;
        }

        if(extname.charAt(0) == '.') {
            extname = extname.substring(1);
        }

        extname = extname.toLowerCase();
        if(extensionName.equals(extname)) {
            return true;
        }

        if(alias != null) {
            for(int i = 0; i < alias.length; i++) {
                if(alias[i].equals(extname)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static FileType getType(byte[] fileHeader) {
        if(fileHeader == null || fileHeader.length == 0) {
            return UNKNOWN;
        }

        String magicString = StringUtils.toHexString(fileHeader);
        FileType resultType = UNKNOWN;
        if(!StringUtils.isEmpty(magicString)) {
            int length = 0;
            for(FileType type : FileType.values()) {
                if(type == UNKNOWN) {
                    continue;
                }

                if(StringUtils.startsWithIgnoreCase(magicString, type.magicHeader)) {
                    int len = type.magicHeader.length();
                    if(len > length) {
                        length = len;
                        resultType = type;
                    }
                }
            }
        }
        return resultType;
    }
    
}
