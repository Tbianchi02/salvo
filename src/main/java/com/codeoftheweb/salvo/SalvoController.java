package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @RequestMapping("/games")
    public Map<String,Object> getGames() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("games", gameRepository.findAll().stream().map(game -> game.makeGameDTO()).collect(toList()));
        return dto;
    }

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @RequestMapping("/game_view/{nn}")
    public Map<String,Object> findGamePlayer(@PathVariable Long nn) {
        GamePlayer gamePlayer = gamePlayerRepository.getById(nn);
        return gamePlayer.makeGameViewDTO();
    }
}