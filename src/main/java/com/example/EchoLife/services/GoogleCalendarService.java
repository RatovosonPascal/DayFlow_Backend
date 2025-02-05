package com.example.EchoLife.services;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.Event;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TimeZone;

@Service
public class GoogleCalendarService {
    private final GoogleCalendarAuthService googleAuthService;

    public GoogleCalendarService(GoogleCalendarAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    /**
     * Récupère les événements à venir aujourd'hui.
     */
    public List<Event> getTodayEvents() throws Exception {
        Calendar service = googleAuthService.getCalendarService();

        // Obtenir les bornes de la journée actuelle
        java.util.Calendar calendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        DateTime startOfDay = new DateTime(calendar.getTime());

        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
        calendar.set(java.util.Calendar.MINUTE, 59);
        calendar.set(java.util.Calendar.SECOND, 59);
        DateTime endOfDay = new DateTime(calendar.getTime());

        // Récupérer les événements de la journée
        Events events = service.events().list("primary")
                .setTimeMin(startOfDay)  // Début de la journée
                .setTimeMax(endOfDay)    // Fin de la journée
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .setMaxResults(10)
                .execute();

        return events.getItems();
    }

    /**
     * Récupère tous les événements futurs à partir de maintenant.
     */
    public List<Event> getFutureEvents(int maxResults) throws Exception {
        Calendar service = googleAuthService.getCalendarService();
        DateTime now = new DateTime(System.currentTimeMillis());

        Events events = service.events().list("primary")
                .setTimeMin(now)  // Exclut les événements passés
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .setMaxResults(maxResults)
                .execute();

        return events.getItems();
    }
}