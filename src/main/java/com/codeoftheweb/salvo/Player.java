package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;            //Definición de instancias
    private String email;

    @OneToMany(mappedBy="playerID", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    /*public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayerID(this);
        gamePlayers.add(gamePlayer);
    }*/

    public Player() { } //Constructor vacío, por default lo necesita Java

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player(String email) {
        this.email = email;   //Solo se la utiliza cuando se quiere instanciar  el programa (ya que empieza con un cierto valor inicial)
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
        return email;
    }

    //@JsonIgnore
    public List<Game> getGames() {
        return gamePlayers.stream().map(sub -> sub.getGameID()).collect(toList());
    }

    public Map<String, Object> makePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id" , this.getId());
        dto.put("email" , this.getEmail());
        return dto;
    }
}
