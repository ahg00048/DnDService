package es.ujaen.ahg00048.microservice_lobby.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
public class PlayerInfo implements Serializable {
    private String id = "";
    private String name = "";


    public PlayerInfo(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((PlayerInfo) obj).id);
    }
}
