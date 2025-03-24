package com.example.ticketHandler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class LogWSHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(LogWSHandler.class);

    private static final String LOG_FILE_PATH = "logs/application.log";

    private WebSocketSession session;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final AtomicBoolean schedulerStarted = new AtomicBoolean(false);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
        logger.info("New WebSocket connection established.");

        sendLogs();

        if (schedulerStarted.compareAndSet(false, true)) {
            startSendingLogsSchedule();
        }
    }

    private void sendLogs() {
        if (this.session == null || !this.session.isOpen()) {
            logger.info("Session is not open, skipping sendLogs.");
            return;
        }

        try {
            List<String> allLines = Files.readAllLines(Paths.get(LOG_FILE_PATH), StandardCharsets.UTF_8);
            int start = Math.max(allLines.size() - 10, 0);
            List<String> last10Lines = allLines.subList(start, allLines.size());
            String last10LinesStr = String.join("\n", last10Lines);

            session.sendMessage(new TextMessage(last10LinesStr));
        } catch (Exception e) {
            logger.error("Error send last 10 log lines: {}", e.getMessage(), e);
        }
    }

    private void startSendingLogsSchedule() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                sendLogs();
            } catch (Exception e) {
                logger.error("Error send last 10 log lines (THREAD): {}", e.getMessage(), e);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("WebSocket connection closed.");
        this.session = null;
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.info("WebSocket transport error: " + exception.getMessage());
        session.close(CloseStatus.SERVER_ERROR);
        this.session = null;
    }
}
