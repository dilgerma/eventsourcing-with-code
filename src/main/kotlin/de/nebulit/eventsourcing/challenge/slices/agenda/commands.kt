package de.nebulit.eventsourcing.challenge.slices.agenda

import de.nebulit.eventsourcing.common.Command
import java.time.LocalTime
import java.util.*


data class NewTasks(val time: LocalTime, val description: String, val points:Int)
data class PlanNewTasksCommand(override var aggregateId: UUID, val tasks: List<NewTasks>) : Command
data class StartScheduleCommand(override var aggregateId: UUID) : Command
