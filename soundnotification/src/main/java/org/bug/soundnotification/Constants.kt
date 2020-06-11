package org.bug.soundnotification

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(NO_INCREMENT, ONE_MINUTE, TWO_MINUTES, THREE_MINUTES, FOUR_MINUTES, FIVE_MINUTES, TEN_MINUTES, DOUBLE, FIB)
annotation class IncrementType

@Retention(AnnotationRetention.SOURCE)
@IntDef(
    NO_LIMIT,
    ONE_MINUTE,
    FIVE_MINUTES,
    FIFTEEN_MINUTES,
    THIRTY_MINUTES,
    ONE_HOUR,
    THREE_HOURS,
    SIX_HOURS,
    TWELVE_HOURS,
    ONE_DAY,
    THREE_NOTIFICATIONS,
    NINE_NOTIFICATIONS,
    TWENTY_FIVE_NOTIFICATIONS,
    FIFTY_NOTIFICATIONS,
    HUNDRED_NOTIFICATIONS
)
annotation class LimitType

const val ONE_MINUTE = 1
const val TWO_MINUTES = 2
const val THREE_MINUTES = 3
const val FOUR_MINUTES = 4
const val FIVE_MINUTES = 5
const val TEN_MINUTES = 10
const val FIFTEEN_MINUTES = 15
const val THIRTY_MINUTES = 30
const val ONE_HOUR = 60
const val THREE_HOURS = 180
const val SIX_HOURS = 360
const val TWELVE_HOURS = 720
const val ONE_DAY = 1440

// increments
const val NO_INCREMENT = 0
const val DOUBLE = -1
const val FIB = -2

// limits
const val NO_LIMIT = 0
const val THREE_NOTIFICATIONS = -3
const val NINE_NOTIFICATIONS = -9
const val TWENTY_FIVE_NOTIFICATIONS = -25
const val FIFTY_NOTIFICATIONS = -50
const val HUNDRED_NOTIFICATIONS = -100

const val PREF_ENABLED = "enable_checkbox"
const val PREF_ACTIVE_NOTIFICATION = "activeId"
const val PREF_UNREAD = "unreadNotifications"
const val PREF_VOLUME_OVERRIDE = "vol_override"
