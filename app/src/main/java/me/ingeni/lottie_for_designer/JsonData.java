package me.ingeni.lottie_for_designer;

/**
 * Created by sungho on 2017. 4. 4..
 */

public class JsonData {
    private final String fileName;
    private final String fileType;

    public JsonData(String fileName, String fileType) {
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFileType() {
        return this.fileType;
    }
}
