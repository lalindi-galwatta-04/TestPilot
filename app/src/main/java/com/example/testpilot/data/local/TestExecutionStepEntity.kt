
package com.example.testpilot.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "test_execution_steps",
    foreignKeys = [
        ForeignKey(
            entity = TestExecutionEntity::class,
            parentColumns = ["id"],
            childColumns = ["executionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("executionId")]
)
data class TestExecutionStepEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val executionId: Int,

    val stepNumber: Int,

    val action: String,

    val expectedResult: String,

    val actualResult: String = "",

    val status: String = "Not Run"
)
