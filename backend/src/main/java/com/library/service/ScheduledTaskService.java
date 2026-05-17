package com.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskService {

    @Autowired
    private LibraryService libraryService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void calculateOverduePenaltyTask() {
        libraryService.calculateOverduePenalty();
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendReturnRemindersTask() {
        libraryService.sendReturnReminders();
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void updateReservationQueueTask() {
        libraryService.updateReservationQueue();
    }
}
