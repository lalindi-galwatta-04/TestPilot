
package com.example.testpilot.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ProjectEntity::class,
        TestSuiteEntity::class,
        TestCaseEntity::class,
        TestStepEntity::class,
        TestExecutionEntity::class,
        TestExecutionStepEntity::class,
        DefectEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class TestPilotDatabase : RoomDatabase() {

    abstract fun projectDao(): ProjectDao

    abstract fun testSuiteDao(): TestSuiteDao

    abstract fun testCaseDao(): TestCaseDao

    abstract fun testStepDao(): TestStepDao

    abstract fun testExecutionDao(): TestExecutionDao

    abstract fun testExecutionStepDao(): TestExecutionStepDao

    abstract fun defectDao(): DefectDao

    companion object {

        @Volatile
        private var INSTANCE: TestPilotDatabase? = null

        // Migration from database version 1 to 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(db: SupportSQLiteDatabase) {

                // Test Suites table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS test_suites (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        projectId INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        FOREIGN KEY(projectId)
                        REFERENCES projects(id)
                        ON UPDATE NO ACTION
                        ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_test_suites_projectId
                    ON test_suites(projectId)
                    """.trimIndent()
                )

                // Test Cases table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS test_cases (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        suiteId INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        preconditions TEXT NOT NULL,
                        priority TEXT NOT NULL,
                        testType TEXT NOT NULL,
                        status TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        FOREIGN KEY(suiteId)
                        REFERENCES test_suites(id)
                        ON UPDATE NO ACTION
                        ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_test_cases_suiteId
                    ON test_cases(suiteId)
                    """.trimIndent()
                )

                // Test Steps table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS test_steps (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        testCaseId INTEGER NOT NULL,
                        stepNumber INTEGER NOT NULL,
                        action TEXT NOT NULL,
                        expectedResult TEXT NOT NULL,
                        FOREIGN KEY(testCaseId)
                        REFERENCES test_cases(id)
                        ON UPDATE NO ACTION
                        ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_test_steps_testCaseId
                    ON test_steps(testCaseId)
                    """.trimIndent()
                )
            }
        }

        // Migration from database version 2 to 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(db: SupportSQLiteDatabase) {

                // Test Executions table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS test_executions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        testCaseId INTEGER NOT NULL,
                        status TEXT NOT NULL,
                        notes TEXT NOT NULL,
                        executedAt INTEGER NOT NULL,
                        FOREIGN KEY(testCaseId)
                        REFERENCES test_cases(id)
                        ON UPDATE NO ACTION
                        ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_test_executions_testCaseId
                    ON test_executions(testCaseId)
                    """.trimIndent()
                )

                // Test Execution Steps table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS test_execution_steps (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        executionId INTEGER NOT NULL,
                        stepNumber INTEGER NOT NULL,
                        action TEXT NOT NULL,
                        expectedResult TEXT NOT NULL,
                        actualResult TEXT NOT NULL,
                        status TEXT NOT NULL,
                        FOREIGN KEY(executionId)
                        REFERENCES test_executions(id)
                        ON UPDATE NO ACTION
                        ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_test_execution_steps_executionId
                    ON test_execution_steps(executionId)
                    """.trimIndent()
                )
            }
        }

        // Migration from database version 3 to 4
        private val MIGRATION_3_4 = object : Migration(3, 4) {

            override fun migrate(db: SupportSQLiteDatabase) {

                // Defects table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS defects (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        severity TEXT NOT NULL,
                        priority TEXT NOT NULL,
                        status TEXT NOT NULL,
                        projectId INTEGER NOT NULL,
                        testCaseId INTEGER,
                        executionId INTEGER,
                        stepsToReproduce TEXT NOT NULL,
                        expectedResult TEXT NOT NULL,
                        actualResult TEXT NOT NULL,
                        reportedAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,

                        FOREIGN KEY(projectId)
                        REFERENCES projects(id)
                        ON UPDATE NO ACTION
                        ON DELETE CASCADE,

                        FOREIGN KEY(testCaseId)
                        REFERENCES test_cases(id)
                        ON UPDATE NO ACTION
                        ON DELETE SET NULL,

                        FOREIGN KEY(executionId)
                        REFERENCES test_executions(id)
                        ON UPDATE NO ACTION
                        ON DELETE SET NULL
                    )
                    """.trimIndent()
                )

                // Project index
                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_defects_projectId
                    ON defects(projectId)
                    """.trimIndent()
                )

                // Test case index
                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_defects_testCaseId
                    ON defects(testCaseId)
                    """.trimIndent()
                )

                // Execution index
                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_defects_executionId
                    ON defects(executionId)
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): TestPilotDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TestPilotDatabase::class.java,
                    "testpilot_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4
                    )
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}
