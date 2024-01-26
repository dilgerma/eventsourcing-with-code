package de.nebulit.eventsourcing.challenge.slices.schedule

import de.nebulit.eventsourcing.common.Command
import java.util.*


data class ScheduleNextTaskCommand(override var aggregateId: UUID) : Command
