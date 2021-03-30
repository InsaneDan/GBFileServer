package ru.isakov;

import  java.io.File ;
import  java.io.Serializable ;

/**
 * Created by haoxy on 2018/11/15.
 * E-mail:hxyHelloWorld @163.com
 * github:https://github.com/haoxiaoyong1014
 */
public  class  FileUploadFile  implements  Serializable {

    private  static  final  long serialVersionUID =  1L ;
    private  File file; // File
    private  String file_md5; // file name
    private  int startPos; // Start position
    private  byte [] bytes; // file byte array
    private  int endPos; // End position

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFile_md5() {
        return file_md5;
    }

    public void setFile_md5(String file_md5) {
        this.file_md5 = file_md5;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }
}