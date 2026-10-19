package es.ujaen.ahg00048.microservice_lobby.service;

import es.ujaen.ahg00048.microservice_lobby.entity.Lobby;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class ScheduleService {
    @Autowired
    @Qualifier("tPoolTaskScheduler")
    private ThreadPoolTaskScheduler _scheduler;

    @Autowired
    private LobbyService _lobbyService;

    @Autowired
    private SimpMessagingTemplate _simpTemplate;

    @Value("${app.lobby.message.timeout}")
    private String _message_lobbyTimeout;


    @Scheduled(fixedDelay = 3, timeUnit = TimeUnit.HOURS)
    public void processUnfinishedTerminations() {
        List<Lobby> lobbies = _lobbyService.getLobbies();

        for (Lobby lobby : lobbies) {
            scheduleLobbyTermination(lobby.getId(), lobby.getTimeout());
        }
    }

    public void scheduleLobbyTermination(String id, Instant timeOfTermination) {
        _scheduler.schedule(() -> {
                _lobbyService.removeLobby(id);
                _simpTemplate.convertAndSend("/topic/lobbies." + id, _message_lobbyTimeout);
            }, timeOfTermination);
    }
}
