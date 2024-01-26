package de.nebulit.eventsourcing.common

import de.nebulit.eventsourcing.common.persistence.InternalEvent
import org.apache.avro.specific.SpecificRecord
import java.time.LocalDateTime
import java.util.UUID

fun SpecificRecord.toInternalRecord(aggreateId: UUID): InternalEvent {
    return InternalEvent().apply {
        this.aggregateId = aggreateId
        this.value = this@toInternalRecord
        this.created = LocalDateTime.now()
    }
}
