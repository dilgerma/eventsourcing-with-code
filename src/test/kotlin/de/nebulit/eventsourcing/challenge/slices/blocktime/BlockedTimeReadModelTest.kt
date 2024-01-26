package de.nebulit.eventsourcing.challenge.slices.blocktime

import de.nebulit.eventsourcing.common.toInternalRecord
import de.nebulit.eventsourcing.testsupport.RandomData
import de.nebulit.eventsourcing.testsupport.ReadModelBddTestBuilder
import de.nebulit.events.additem.GoalAdded
import de.nebulit.events.blocktime.SchedulePlanned
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class BlockedTimeReadModelTest {

    @Test
    fun `provides blocked time`() {

        val aggregateId: UUID = UUID.randomUUID()
        val blockedTimeInMinutes = 120
        val addedEvent = RandomData.newInstance<GoalAdded> {
            this.aggregateId = aggregateId
        }
        ReadModelBddTestBuilder.take(BlockedTimeReadModel())
            .given(
                SchedulePlanned(aggregateId, blockedTimeInMinutes).toInternalRecord(aggregateId),
                addedEvent.toInternalRecord(aggregateId)
            )
            .expect {
                assertThat(it.timeBlocked).isEqualTo(blockedTimeInMinutes)
                assertThat(it.items).containsEntry(
                    addedEvent.itemId,
                    addedEvent.description.toString()
                )
            }
    }

    @Test
    fun `model has no blockedTime if schedule was not started`() {

        val blockedTimeInMinutes = 0

        ReadModelBddTestBuilder.take(BlockedTimeReadModel())
            .given()
            .expect {
                assertThat(it.timeBlocked).isEqualTo(blockedTimeInMinutes)
            }
    }
}
