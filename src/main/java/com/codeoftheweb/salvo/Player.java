package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;            //Definición de instancias
    private String email;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<Score> scores;

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    /*public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayerID(this);
        gamePlayers.add(gamePlayer);
    }*/

    public Player() { } //Constructor vacío, por default lo necesita Java

    public Player(String email) {
        this.email = email;   //Solo se la utiliza cuando se quiere instanciar  el programa (ya que empieza con un cierto valor inicial)
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //@JsonIgnore
    public List<Game> getGames() {
        return gamePlayers.stream().map(game -> game.getGame()).collect(toList());
    }

    public Optional<Score> getScore(Game game){
        return this.getScores().stream().filter(score -> score.getGame().getId().equals(game.getId())).findFirst();
    }

    public Map<String, Object> makePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("email", this.getEmail());
        return dto;
    }
}
