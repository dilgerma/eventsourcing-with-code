package de.nebulit.eventsourcing.challenge.slices.finishtask

import de.nebulit.eventsourcing.common.toInternalRecord
import de.nebulit.eventsourcing.testsupport.ReadModelBddTestBuilder
import de.nebulit.events.additem.GoalAdded
import de.nebulit.events.finishgoal.TaskFinished
import de.nebulit.events.itemscheduler.TaskScheduled
import de.nebulit.events.schedule.TaskPlanned
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class FinishedItemsReadModelTest {

    @Test
    fun `holds finished items`() {

        val aggregateId: UUID = UUID.randomUUID()
        val itemId: UUID = UUID.randomUUID()
        val description: String = "description"

        ReadModelBddTestBuilder.take(FinishedItemsReadModel())
            .given(
                GoalAdded().apply {
                    this.aggregateId = aggregateId
                    this.itemId = itemId
                    this.description = description
                }.toInternalRecord(aggregateId),
                TaskPlanned().apply {
                    this.aggregateId = aggregateId
                    this.itemId = itemId
                    this.description="description"
                }.toInternalRecord(aggregateId),
                TaskScheduled().apply {
                    this.aggregateId = aggregateId
                    this.itemId = itemId
                }.toInternalRecord(aggregateId),
                TaskFinished().apply {
                    this.aggregateId = aggregateId
                    this.itemId = itemId
                }.toInternalRecord(aggregateId)
            )
            .expect {
                assertThat(it.items).contains(Task(itemId, description, true))
            }
    }

    @Test
    fun `empty if no items have been finished`() {

        val aggregateId: UUID = UUID.randomUUID()
        val itemId: UUID = UUID.randomUUID()
        val description = "description"

        ReadModelBddTestBuilder.take(FinishedItemsReadModel())
            .given(
                GoalAdded().apply {
                    this.aggregateId = aggregateId
                    this.itemId = itemId
                    this.description = description
                }.toInternalRecord(aggregateId),
                TaskScheduled().apply {
                    this.aggregateId = aggregateId
                    this.itemId = itemId
                }.toInternalRecord(aggregateId)
            )
            .expect {
                assertThat(it.items).isEmpty()
            }
    }
}

