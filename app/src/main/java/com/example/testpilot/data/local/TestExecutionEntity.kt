
package com.example.testpilot.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "test_executions",
    foreignKeys = [
        ForeignKey(
            entity = TestCaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["testCaseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("testCaseId")]
)
data class TestExecutionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val testCaseId: Int,

    val status: String = "Not Run",

    val notes: String = "",

    val executedAt: Long = System.currentTimeMillis()
)
