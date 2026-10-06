package me.rerere.rikkahub.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Upstream-compatibility repair for restoring a backup exported by the official app.
 *
 * The official app and this fork occupied the same database version numbers (24~27) with
 * different table layouts, so an official backup runs through this fork's migrations and
 * still ends up not matching what this app expects; Room then aborts the restore with
 * "Migration didn't properly handle".
 *
 * This migration closes the remaining gap and is defensive on purpose: missing columns and
 * tables are added, official-only columns are removed, and everything is a no-op when the
 * database already matches this fork's layout (e.g. this app's own v27 database).
 */
val Migration_27_28 = object : Migration(27, 28) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // ConversationEntity: official layout lacks the three auto-compression columns.
        addColumnIfMissing(
            db,
            tableName = "ConversationEntity",
            columnName = "compressed_summary",
            sql = "ALTER TABLE `ConversationEntity` ADD COLUMN `compressed_summary` TEXT NOT NULL DEFAULT ''",
        )
        addColumnIfMissing(
            db,
            tableName = "ConversationEntity",
            columnName = "compressed_node_ids",
            sql = "ALTER TABLE `ConversationEntity` ADD COLUMN `compressed_node_ids` TEXT NOT NULL DEFAULT '[]'",
        )
        addColumnIfMissing(
            db,
            tableName = "ConversationEntity",
            columnName = "auto_compress_config",
            sql = "ALTER TABLE `ConversationEntity` ADD COLUMN `auto_compress_config` TEXT NOT NULL DEFAULT ''",
        )

        // ConversationEntity: folder_id is an official-only column.
        dropColumnIfExists(
            db,
            tableName = "ConversationEntity",
            columnName = "folder_id",
            sql = "ALTER TABLE `ConversationEntity` DROP COLUMN `folder_id`",
        )

        // workspaces: shell_compatibility_mode is an official-only column.
        dropColumnIfExists(
            db,
            tableName = "workspaces",
            columnName = "shell_compatibility_mode",
            sql = "ALTER TABLE `workspaces` DROP COLUMN `shell_compatibility_mode`",
        )

        // Fork-only tables absent from official backups.
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `moments` (
                `id` TEXT NOT NULL,
                `assistant_id` TEXT NOT NULL,
                `author` TEXT NOT NULL,
                `content` TEXT NOT NULL,
                `context_note` TEXT NOT NULL,
                `image_description` TEXT NOT NULL,
                `images` TEXT NOT NULL,
                `reply_due_at` INTEGER NOT NULL,
                `reply_status` TEXT NOT NULL,
                `ai_liked` INTEGER NOT NULL,
                `ai_reply_content` TEXT NOT NULL,
                `replied_at` INTEGER,
                `ai_reply_seen_at` INTEGER,
                `user_liked` INTEGER NOT NULL,
                `created_at` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `moment_comments` (
                `id` TEXT NOT NULL,
                `moment_id` TEXT NOT NULL,
                `author` TEXT NOT NULL,
                `content` TEXT NOT NULL,
                `reply_due_at` INTEGER,
                `reply_status` TEXT NOT NULL,
                `seen_at` INTEGER,
                `created_at` INTEGER NOT NULL,
                PRIMARY KEY(`id`),
                FOREIGN KEY(`moment_id`) REFERENCES `moments`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `moment_profiles` (
                `assistant_id` TEXT NOT NULL,
                `cover_uri` TEXT NOT NULL,
                `last_viewed_at` INTEGER NOT NULL,
                PRIMARY KEY(`assistant_id`)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_moments_assistant_id_created_at` ON `moments` (`assistant_id`, `created_at`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_moments_assistant_id_reply_status_reply_due_at` ON `moments` (`assistant_id`, `reply_status`, `reply_due_at`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_moment_comments_moment_id_created_at` ON `moment_comments` (`moment_id`, `created_at`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_moment_comments_author_reply_status_reply_due_at` ON `moment_comments` (`author`, `reply_status`, `reply_due_at`)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `anonymous_questions` (
                `id` TEXT NOT NULL, `scope_id` TEXT NOT NULL, `author` TEXT NOT NULL,
                `content` TEXT NOT NULL, `reply_due_at` INTEGER, `reply_status` TEXT NOT NULL,
                `created_at` INTEGER NOT NULL, PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `anonymous_question_replies` (
                `id` TEXT NOT NULL, `question_id` TEXT NOT NULL, `author` TEXT NOT NULL,
                `kind` TEXT NOT NULL, `content` TEXT NOT NULL, `reply_due_at` INTEGER,
                `reply_status` TEXT NOT NULL, `created_at` INTEGER NOT NULL, PRIMARY KEY(`id`),
                FOREIGN KEY(`question_id`) REFERENCES `anonymous_questions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `anonymous_question_profiles` (
                `scope_id` TEXT NOT NULL, `last_viewed_at` INTEGER NOT NULL, PRIMARY KEY(`scope_id`)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_anonymous_questions_scope_id_created_at` ON `anonymous_questions` (`scope_id`, `created_at`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_anonymous_questions_scope_id_author_reply_status_reply_due_at` ON `anonymous_questions` (`scope_id`, `author`, `reply_status`, `reply_due_at`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_anonymous_question_replies_question_id_created_at` ON `anonymous_question_replies` (`question_id`, `created_at`)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_anonymous_question_replies_question_id_author_kind` ON `anonymous_question_replies` (`question_id`, `author`, `kind`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_anonymous_question_replies_author_reply_status_reply_due_at` ON `anonymous_question_replies` (`author`, `reply_status`, `reply_due_at`)")
    }
}

private fun addColumnIfMissing(
    db: SupportSQLiteDatabase,
    tableName: String,
    columnName: String,
    sql: String,
) {
    if (!hasColumn(db, tableName, columnName)) {
        db.execSQL(sql)
    }
}

private fun dropColumnIfExists(
    db: SupportSQLiteDatabase,
    tableName: String,
    columnName: String,
    sql: String,
) {
    if (hasColumn(db, tableName, columnName)) {
        db.execSQL(sql)
    }
}

private fun hasColumn(db: SupportSQLiteDatabase, tableName: String, columnName: String): Boolean {
    db.query("PRAGMA table_info(`$tableName`)").use { cursor ->
        val nameIndex = cursor.getColumnIndex("name")
        while (cursor.moveToNext()) {
            if (cursor.getString(nameIndex) == columnName) {
                return true
            }
        }
    }
    return false
}
