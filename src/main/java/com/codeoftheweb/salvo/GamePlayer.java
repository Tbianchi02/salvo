package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="playerID")
    private Player playerID;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gameID")
    private Game gameID;

    public GamePlayer() { }

    public GamePlayer(Game gameID, Player playerID, LocalDateTime joinDate) {
        this.gameID = gameID;
        this.playerID = playerID;
        this.joinDate = joinDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public Player getPlayerID() {
        return playerID;
    }

    public void setPlayerID(Player playerID) {
        this.playerID = playerID;
    }

    public Game getGameID() {
        return gameID;
    }

    public void setGameID(Game gameID) {
        this.gameID = gameID;
    }

    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id" , this.getId());
        dto.put("player", this.getPlayerID().makePlayerDTO());
        return dto;
    }
}