package com.tecknobit.novacore.records;

import java.util.List;

/**
 * The {@code NotificationsTarget} instance is useful to fetch the notifications of a specific {@link NovaItem}'s target
 *
 * @author N7ghtm4r3 - Tecknobit
 */
public interface NotificationsTarget {

    /**
     * Method to count the notifications of a specific target
     *
     * @param notifications: the list of notifications to check
     *
     * @return the count of the notifications for the specific target as int
     */
    int getNotifications(List<NovaNotification> notifications);

}
