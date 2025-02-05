package com.example.EchoLife.controllers;

import com.example.EchoLife.services.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    private final GoogleCalendarService googleCalendarService;

    public CalendarController(GoogleCalendarService googleCalendarService) {
        this.googleCalendarService = googleCalendarService;
    }

    @GetMapping("/TodayEvents")
    public List<Event> getTodayEvents() throws Exception {
        return googleCalendarService.getTodayEvents();
    }
    @GetMapping("/FutureEvents")
    public List<Event> getFutureEvents() throws Exception {
        return googleCalendarService.getFutureEvents(10);
    }
}
