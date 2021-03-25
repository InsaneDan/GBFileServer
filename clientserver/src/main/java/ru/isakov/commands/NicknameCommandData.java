package ru.isakov.commands;

import java.io.Serializable;

public class NicknameCommandData implements Serializable {

    private final String nickname;

    public NicknameCommandData(String message) {
        this.nickname = message;
    }

    public String getNickname() {
        return nickname;
    }

}
