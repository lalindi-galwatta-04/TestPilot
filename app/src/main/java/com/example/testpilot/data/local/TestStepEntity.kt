
package com.example.testpilot.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "test_steps",
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
data class TestStepEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val testCaseId: Int,

    val stepNumber: Int,

    val action: String,

    val expectedResult: String
)
