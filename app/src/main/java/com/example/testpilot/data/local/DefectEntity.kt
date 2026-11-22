
package com.example.testpilot.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "defects",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TestCaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["testCaseId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = TestExecutionEntity::class,
            parentColumns = ["id"],
            childColumns = ["executionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("projectId"),
        Index("testCaseId"),
        Index("executionId")
    ]
)
data class DefectEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val description: String,

    val severity: String = "Medium",

    val priority: String = "Medium",

    val status: String = "Open",

    val projectId: Int,

    val testCaseId: Int? = null,

    val executionId: Int? = null,

    val stepsToReproduce: String = "",

    val expectedResult: String = "",

    val actualResult: String = "",

    val reportedAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()
)
