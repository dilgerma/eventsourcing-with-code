package de.nebulit.eventsourcing.challenge.domain

import de.nebulit.eventsourcing.common.*
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.eventsourcing.challenge.slices.addgoal.AddGoalCommand
import de.nebulit.eventsourcing.challenge.domain.addgoal.AddGoalState
import de.nebulit.eventsourcing.challenge.domain.challengestart.ChallengeStartState
import de.nebulit.eventsourcing.challenge.domain.finishtask.FinishGoalState
import de.nebulit.eventsourcing.challenge.domain.itemscheduler.PlannedTaskState
import de.nebulit.eventsourcing.challenge.domain.plangoal.AddedGoalState
import de.nebulit.eventsourcing.challenge.domain.schedule.StartSchedule
import de.nebulit.eventsourcing.challenge.slices.agenda.PlanNewTasksCommand
import de.nebulit.eventsourcing.challenge.slices.agenda.StartScheduleCommand
import de.nebulit.eventsourcing.challenge.slices.blocktime.BlockTimeCommand
import de.nebulit.eventsourcing.challenge.slices.challengestart.StartChallengeCommand
import de.nebulit.eventsourcing.challenge.slices.finishtask.FinishTaskCommand
import de.nebulit.eventsourcing.challenge.slices.schedule.ScheduleNextTaskCommand
import de.nebulit.events.additem.GoalAdded
import de.nebulit.events.blocktime.SchedulePlanned
import de.nebulit.events.challengestart.ChallengeStarted
import de.nebulit.events.finishgoal.TaskFinished
import de.nebulit.events.finishtask.ScheduleFinished
import de.nebulit.events.itemscheduler.TaskDiscarded
import de.nebulit.events.itemscheduler.TaskScheduled
import de.nebulit.events.schedule.ScheduleStarted
import de.nebulit.events.schedule.TaskPlanned
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.JdbcTypeCode
import java.sql.Types
import java.util.UUID


@Entity
@Table(name = "todo")
class Challenge(@JdbcTypeCode(Types.VARCHAR) @Id override var aggregateId: UUID) : AggregateRoot {

    //hibernate
    protected constructor() : this(UUID.randomUUID())

    @Version
    override var version: Long? = null

    @Transient
    override var events: MutableList<InternalEvent> = mutableListOf()

    @Transient
    private var addGoalState = AddGoalState()

    @Transient
    private var addedGoalState = AddedGoalState()

    @Transient
    private var startSchedule = StartSchedule()

    @Transient
    private var plannedGoalState = PlannedTaskState()

    @Transient
    private var finishGoalState = FinishGoalState()

    @Transient
    private var challengeStartState = ChallengeStartState()

    override fun applyEvents(events: List<InternalEvent>): Challenge {
        addGoalState.applyEvents(events)
        addedGoalState.applyEvents(events)
        startSchedule.applyEvents(events)
        plannedGoalState.applyEvents(events)
        finishGoalState.applyEvents(events)
        challengeStartState.applyEvents(events)
        return this
    }

    fun handle(command: AddGoalCommand) {
        if (addGoalState.goals.size >= 5) {
            throw CommandException("cannot have more than 5 TODOs")
        }
        if (addGoalState.goals.firstOrNull { it.id == command.id } != null) {
            throw CommandException("cannot add same if twice")

        }
        if (addGoalState.goals.firstOrNull { it.description == command.description } != null) {
            throw CommandException("cannot add same description twice")

        }

        events.add(GoalAdded().apply {
            this.aggregateId = command.aggregateId
            this.itemId = command.id
            this.description = command.description
        }.toInternalRecord(command.aggregateId))
    }

    fun handle(command: BlockTimeCommand) {
        if (addedGoalState.addedGoals.isEmpty()) {
            throw CommandException("Cannot start schedule without items.")
        }
        events.add(SchedulePlanned().apply {
            this.aggregateId = command.aggregateId
            this.durationInMinutes = command.minutes
        }.toInternalRecord(command.aggregateId))
    }

    fun handle(command: StartScheduleCommand) {
        if (startSchedule.plannedTasks.isEmpty()) {
            throw CommandException("cannot start schedule without planned goals")
        }
        events.add(ScheduleStarted().apply { this.aggregateId = command.aggregateId }
                .toInternalRecord(command.aggregateId))
    }

    fun handle(scheduleGoalCommand: ScheduleNextTaskCommand) {
        var scheduleableItems = plannedGoalState.plannedTasks.filter { it.canBeScheduled() }.toList()
        scheduleableItems.forEachIndexed { index, item ->
            if (index < scheduleableItems.lastIndex) {
                // discard all but the last
                events.add(TaskDiscarded(aggregateId, item.itemId).toInternalRecord(aggregateId))
                if (plannedGoalState.plannedTasks.isEmpty() && plannedGoalState.activeTask == null) {
                    events.add(ScheduleFinished(aggregateId).toInternalRecord(aggregateId))
                }
            } else {
                events.add(TaskScheduled(aggregateId, item.itemId).toInternalRecord(aggregateId))
                if (plannedGoalState.activeTask != null && item.itemId != plannedGoalState.activeTask) {
                    //discard any active tasks after the new schedule
                    events.add(TaskDiscarded(aggregateId, plannedGoalState.activeTask).toInternalRecord(aggregateId))
                    if (plannedGoalState.plannedTasks.isEmpty() && plannedGoalState.activeTask == null) {
                        events.add(ScheduleFinished(aggregateId).toInternalRecord(aggregateId))
                    }
                }

            }
        }




    }

    fun handle(finishTask: FinishTaskCommand) {

        if (finishGoalState.currentlyActiveTask != finishTask.itemId) {
            throw CommandException("Can only finish active items")
        }

        events.add(TaskFinished().apply {
            this.aggregateId = finishTask.aggregateId
            this.itemId = finishTask.itemId
        }.toInternalRecord(aggregateId))

        if (plannedGoalState.plannedTasks.isEmpty()) {
            events.add(ScheduleFinished(aggregateId).toInternalRecord(aggregateId))
        }

    }

    fun handle(planNewTaskCommand: PlanNewTasksCommand) {
        if (hasDuplicateDescription(planNewTaskCommand)) {
            throw CommandException("cannot schedule duplicate descriptions")
        }
        if (planNewTaskCommand.tasks.isEmpty()) {
            throw CommandException("cannot plan empty tasks")
        }
        planNewTaskCommand.tasks.forEach { item ->
            events.add(TaskPlanned().apply {
                this.aggregateId = planNewTaskCommand.aggregateId
                this.itemId = UUID.randomUUID()
                this.starttime = item.time
                this.description = item.description
                this.points = item.points
            }.toInternalRecord(planNewTaskCommand.aggregateId))
        }

    }

    fun start(startChallengeCommand: StartChallengeCommand) {
        if (challengeStartState.activeItems.contains(startChallengeCommand.aggregateId)) {
            throw CommandException("challenge ${startChallengeCommand.aggregateId} already exists")
        }
        this.events.add(ChallengeStarted(startChallengeCommand.aggregateId).toInternalRecord(startChallengeCommand.aggregateId))
    }


}

private fun hasDuplicateDescription(planNewTasksCommand: PlanNewTasksCommand) =
        planNewTasksCommand.tasks.groupBy { it.description }.values.any { it.size > 1 }
