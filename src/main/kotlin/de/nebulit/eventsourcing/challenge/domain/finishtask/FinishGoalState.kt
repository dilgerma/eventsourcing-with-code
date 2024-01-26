package de.nebulit.eventsourcing.challenge.domain.finishtask

import de.nebulit.eventsourcing.common.EventState
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.events.finishgoal.TaskFinished
import de.nebulit.events.itemscheduler.TaskDiscarded
import de.nebulit.events.itemscheduler.TaskScheduled
import de.nebulit.events.schedule.TaskPlanned
import java.util.*

class FinishGoalState : EventState<FinishGoalState> {

    var currentlyActiveTask: UUID? = null
    val activeTasks = mutableListOf<UUID>()
    override fun applyEvents(events: List<InternalEvent>): FinishGoalState {
        events.forEach {
            when (it.value) {
                is TaskPlanned -> {
                    activeTasks.add((it.value as TaskPlanned).itemId)
                }

                is TaskScheduled -> {
                    this.currentlyActiveTask = (it.value as TaskScheduled).itemId
                }

                is TaskDiscarded -> {
                    if (this.currentlyActiveTask == (it.value as TaskDiscarded).itemId) currentlyActiveTask = null
                    this.activeTasks.removeIf { item -> (it.value as TaskDiscarded).itemId == item }
                }

                is TaskFinished -> {
                    this.activeTasks.removeIf { item -> (it.value as TaskFinished).itemId == item }
                }
            }
        }
        return this
    }
}
