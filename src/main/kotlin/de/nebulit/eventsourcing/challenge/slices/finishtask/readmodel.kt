package de.nebulit.eventsourcing.challenge.slices.finishtask

import de.nebulit.eventsourcing.common.ReadModel
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.events.finishgoal.TaskFinished
import de.nebulit.events.itemscheduler.TaskDiscarded
import de.nebulit.events.itemscheduler.TaskScheduled
import de.nebulit.events.schedule.TaskPlanned
import java.util.*

data class Task(val itemId: UUID, val description: String, var finished: Boolean)

class ActiveTaskReadModel : ReadModel<ActiveTaskReadModel> {

    var task: Task? = null

    override fun applyEvents(events: List<InternalEvent>): ActiveTaskReadModel {

        val descriptions = mutableMapOf<UUID, String>()
        events.forEach {
            when (it.value) {
                is TaskPlanned -> descriptions.put((it.value as TaskPlanned).itemId, (it.value as TaskPlanned).description.toString())
                is TaskScheduled -> task = Task((it.value as TaskScheduled).itemId, descriptions.get((it.value as TaskScheduled).itemId)
                        ?: "", false)
                is TaskFinished -> if(task?.itemId == (it.value as TaskFinished).itemId) task = null
                is TaskDiscarded -> if(task?.itemId == (it.value as TaskDiscarded).itemId) task = null
            }
        }
        return this
    }

}

class FinishedItemsReadModel : ReadModel<FinishedItemsReadModel> {

    var items = listOf<Task>()

    override fun applyEvents(events: List<InternalEvent>): FinishedItemsReadModel {
        val tasks = mutableListOf<Task>()
        events.forEach {
            when (it.value) {
                is TaskPlanned -> tasks.add(
                        Task(
                                (it.value as TaskPlanned).itemId,
                                (it.value as TaskPlanned).description.toString(),
                                false
                        )
                )

                is TaskFinished -> tasks.find { item -> (it.value as TaskFinished).itemId == item.itemId }?.finished = true
            }
        }
        this.items = tasks.filter { it.finished }
        return this
    }
}
