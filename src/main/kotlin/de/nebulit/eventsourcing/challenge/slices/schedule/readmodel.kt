package de.nebulit.eventsourcing.challenge.slices.schedule

import de.nebulit.eventsourcing.common.ProcessorReadModel
import de.nebulit.eventsourcing.common.ReadModel
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.events.blocktime.SchedulePlanned
import de.nebulit.events.finishtask.ScheduleFinished
import de.nebulit.events.itemscheduler.TaskDiscarded
import de.nebulit.events.itemscheduler.TaskScheduled
import de.nebulit.events.schedule.ScheduleStarted
import de.nebulit.events.schedule.TaskPlanned
import java.time.LocalTime
import java.util.*



class ScheduleStatusReadModel : ReadModel<ScheduleStatusReadModel> {

    var schedulePlanned: Boolean = false
    var scheduleStarted: Boolean = false

    override fun applyEvents(events: List<InternalEvent>): ScheduleStatusReadModel {
        events.forEach {
            when (it.value) {
                is SchedulePlanned -> schedulePlanned = true
                is ScheduleStarted -> scheduleStarted = true
            }
        }
        return this
    }

}



class ActiveSchedulesReadModel : ProcessorReadModel<ActiveSchedulesReadModel> {

    var activeSchedules = mutableListOf<UUID>()

    override fun applyEvents(events: List<InternalEvent>): ActiveSchedulesReadModel {
        events.forEach {
            when (it.value) {
                is ScheduleStarted -> activeSchedules.add((it.value as ScheduleStarted).aggregateId)
                is ScheduleFinished -> activeSchedules.removeIf {item -> item == (it.value as ScheduleFinished).aggregateId}
            }
        }
        return this
    }

}

data class ScheduledTask(var taskId: UUID, val startTime: LocalTime)

data class ActivePlannedTask(var taskId: UUID, var description: String, var duration: Int, val startTime: LocalTime)
class ActivePlannedTasksReadModel : ReadModel<ActivePlannedTasksReadModel> {

    var plannedTasks = listOf<ActivePlannedTask>()

    override fun applyEvents(events: List<InternalEvent>): ActivePlannedTasksReadModel {
        var scheduledTasks: MutableMap<UUID, ActivePlannedTask> = mutableMapOf()

        events.forEach {
            when (it.value) {
                is TaskPlanned -> scheduledTasks.put(
                    (it.value as TaskPlanned).itemId, ActivePlannedTask((it.value as TaskPlanned).itemId,
                        (it.value as TaskPlanned).description.toString(),
                        (it.value as TaskPlanned).durationInMinutes, (it.value as TaskPlanned).starttime))
                is TaskDiscarded -> scheduledTasks.remove((it.value as TaskDiscarded).itemId)
                is TaskScheduled -> scheduledTasks.remove((it.value as TaskScheduled).itemId)
            }
        }
        this.plannedTasks = scheduledTasks.values.toList()
        return this
    }
}