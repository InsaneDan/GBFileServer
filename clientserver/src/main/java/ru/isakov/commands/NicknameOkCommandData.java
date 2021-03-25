package ru.isakov.commands;

import java.io.Serializable;

public class NicknameOkCommandData implements Serializable {

    private final String nickname;

    public NicknameOkCommandData(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

}
