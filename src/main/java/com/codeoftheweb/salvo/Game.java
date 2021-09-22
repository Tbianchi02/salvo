package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private LocalDateTime creationDate;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<Score> scores;

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public Game() { }

    public Game(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    //@JsonIgnore
    public List<Player> getPlayers() {
        return gamePlayers.stream().map(player -> player.getPlayer()).collect(toList());
    }

    public Map<String, Object> makeGameDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("created", this.getCreationDate());
        dto.put("gamePlayers", this.getGamePlayers().stream().map(player -> player.makeGamePlayerDTO()).collect(toList()));
        //dto.put("score", this.getScores().stream().map(score -> score.makeScoreDTO()).collect(toList())); Esto funciona sin los métodos más complicados -> "manera fácil"
        dto.put("scores", this.getGamePlayers().stream().map(gamePlayer -> {
            if(gamePlayer.getScore().isPresent()){ return gamePlayer.getScore().get().makeScoreDTO(); }
            else { return "No finalizado"; }    //En caso de que no haya resultados de algunos juegos
        }));
        return dto; //Esto funciona con los métodos más complicados -> "manera difícil"
    }
}