package de.nebulit.eventsourcing.challenge.domain.itemscheduler

import de.nebulit.eventsourcing.common.EventState
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.events.finishgoal.TaskFinished
import de.nebulit.events.itemscheduler.TaskDiscarded
import de.nebulit.events.itemscheduler.TaskScheduled
import de.nebulit.events.schedule.TaskPlanned
import java.time.LocalTime
import java.util.*

data class ScheduleableItem(val itemId: UUID, val startTime: LocalTime, val durationInMinutes: Long) {
    fun canBeScheduled(): Boolean {
        return startTime <= LocalTime.now()
    }
}

class PlannedTaskState : EventState<PlannedTaskState> {
    var plannedTasks: MutableList<ScheduleableItem> = mutableListOf()
    var activeTask: UUID? = null
    override fun applyEvents(events: List<InternalEvent>): PlannedTaskState {
        events.forEach {
            when (it.value) {
                is TaskPlanned -> plannedTasks.add(
                        ScheduleableItem(
                                (it.value as TaskPlanned).itemId,
                                (it.value as TaskPlanned).starttime,
                                (it.value as TaskPlanned).durationInMinutes.toLong()
                        )
                )

                is TaskScheduled -> {
                    plannedTasks.removeIf { item -> item.itemId == (it.value as TaskScheduled).itemId }
                    activeTask = (it.value as TaskScheduled).itemId
                }

                is TaskFinished -> {
                    plannedTasks.removeIf { item -> item.itemId == (it.value as TaskFinished).itemId }
                    if ((it.value as TaskFinished).itemId == activeTask) activeTask = null
                }

                is TaskDiscarded -> {
                    plannedTasks.removeIf { item -> item.itemId == (it.value as TaskDiscarded).itemId }
                    if ((it.value as TaskDiscarded).itemId == activeTask) activeTask = null
                }

            }
        }
        return this
    }

}
