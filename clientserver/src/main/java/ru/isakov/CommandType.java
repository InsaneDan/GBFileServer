package ru.isakov;

public enum CommandType {
    AUTH,
    AUTH_OK,
    AUTH_ERR,
    REG,
    REG_OK,
    REG_ERR,
    FILE_LIST,
    FILE_UPLOAD,
    FILE_DOWNLOAD,
    FILE_TRANSFER,
    FILE_TRANSFER_END,
    DIR_DELETE,
    MESSAGE,
    ERROR,
    EXIT;

}
