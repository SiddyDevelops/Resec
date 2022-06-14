package com.siddydevelops.sms_kotlin.utils


object Constants {
    const val ACTIVE = "Resec"
    const val INACTIVE = "Resec.Inactive"

    const val COMMANDS = "Resec.Commands"
    const val MESSAGE_COMMANDS0 = "Resec Commands are as follows:"
    const val MESSAGE_COMMANDS1 = "●<Resec.Lock>: Lock-Screen immediately."
    const val MESSAGE_COMMANDS2 = "●<Resec.Contacts<UserID><UserPIN>>"
    const val MESSAGE_COMMANDS3 = "●<Resec.Location>: Get device location."
    const val MESSAGE_COMMANDS4 = "●<Resec.SoundStatus>: Check the status of sound profile."
    const val MESSAGE_COMMANDS5 = "●<Resec.Inactive>: Terminate Resec services."

    const val SOUND_PROFILE_STATUS = "Resec.SoundStatus"
    const val SOUND_PROFILE_NORMAL = "Resec.SoundNormal"

    const val LOCATION_COMMAND = "Resec.Location"

    const val LOCK_COMMAND = "Resec.Lock"

    const val CONTACTS_COMMANDS = "Use Resec.ContactName<Name> to retrieve a particular contact by name.\nUse Resec.Contacts<alphabet> to get list of contacts beginning with particular alphabet."

    const val SEND_ACK = "Resec is Active.\nUse <Resec.About> to know more."
    const val SEND_NACK = "Resec is InActive.\nUse <Resec> to activate or using android app toggle resec state."
    const val HELP = "Resec.About"

    const val TRY_AGAIN = "Could not understand your request.Please try again."

    const val INVALID_CREDS = "Credentials provided are invalid. Please ensure Resec services are active."

    const val MESSAGE_ABOUT = "Resec helps you to access your android application anytime remotely. Please type <Resec.Commands> to get list of available commands.>"

    const val EXTRA_ACTIVE = "EXTRA_ACTIVE"
    const val EXTRA_SOUND_PROFILE = "EXTRA_SOUND_PROFILE"
    const val EXTRA_VOL_RING = "EXTRA_VOL_RING"
    const val EXTRA_VOL_MEDIA = "EXTRA_VOL_MEDIA"
    const val EXTRA_SOUND_NOTIFICATION = "EXTRA_SOUND_NOTIFICATION"
    const val EXTRA_BRIGHTNESS = "EXTRA_BRIGHTNESS"
    const val EXTRA_START_TIME = "EXTRA_START_TIME"
}