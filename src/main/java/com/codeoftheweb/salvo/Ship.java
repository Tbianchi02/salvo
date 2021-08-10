package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private String type;

    @ElementCollection
    @Column(name="locations")
    private List<String> locations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayerID")
    private GamePlayer gamePlayerID;

    public Ship() { }

    public Ship(String type, List<String> locations, GamePlayer gamePlayerID) {
        this.type = type;
        this.locations = locations;
        this.gamePlayerID = gamePlayerID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GamePlayer getGamePlayerID() {
        return gamePlayerID;
    }

    public void setGamePlayerID(GamePlayer gamePlayerID) {
        this.gamePlayerID = gamePlayerID;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    /*@JsonIgnore
    public List<GamePlayer> getShips() {
        return ships.stream().map(gamePlayer -> gamePlayer.getGamePlayerID()).collect(toList());
    }           REVISAR*/
}
