package de.nebulit.eventsourcing.challenge.domain.schedule

import de.nebulit.eventsourcing.common.EventState
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.events.additem.GoalAdded
import de.nebulit.events.schedule.TaskPlanned
import java.util.*

class StartSchedule: EventState<StartSchedule> {

    var addedGoals: MutableList<UUID> = mutableListOf()
    var plannedTasks: MutableList<UUID> = mutableListOf()

    override fun applyEvents(events: List<InternalEvent>): StartSchedule {
        events.forEach {
            when (it.value) {
                is GoalAdded -> addedGoals.add((it.value as GoalAdded).itemId)
                is TaskPlanned -> plannedTasks.add((it.value as TaskPlanned).itemId)
            }
        }
        return this
    }

    fun allGoalsPlanned(): Boolean {
        return addedGoals.isNotEmpty() && addedGoals.containsAll(plannedTasks)
    }

}
