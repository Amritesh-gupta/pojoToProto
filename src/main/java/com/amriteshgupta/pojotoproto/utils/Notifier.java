package com.amriteshgupta.pojotoproto.utils;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for showing notifications
 */
public class Notifier {

    /**
     * Show notification
     *
     * @param project Project
     * @param content Notification content
     * @param type    Notification type
     */
    public static void notify(@Nullable Project project, String content, NotificationType type) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("com.github.amriteshgupta.pojotoproto.NotificationGroup")
                .createNotification(content, type)
                .notify(project);
    }

    /**
     * Show information notification
     *
     * @param project Project
     * @param content Notification content
     */
    public static void notifyInfo(@Nullable Project project, String content) {
        notify(project, content, NotificationType.INFORMATION);
    }

    /**
     * Show error notification
     *
     * @param project Project
     * @param content Notification content
     */
    public static void notifyError(@Nullable Project project, String content) {
        notify(project, content, NotificationType.ERROR);
    }

    /**
     * Show warning notification
     *
     * @param project Project
     * @param content Notification content
     */
    public static void notifyWarning(@Nullable Project project, String content) {
        notify(project, content, NotificationType.WARNING);
    }
}
