package br.com.devfocus.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import br.com.devfocus.data.local.dao.QuoteDao
import br.com.devfocus.data.local.dao.StudyDao
import br.com.devfocus.data.local.entity.QuoteEntity
import br.com.devfocus.data.local.entity.StudyDayEntity
import br.com.devfocus.data.local.entity.StudyStatus

@Database(
    entities = [QuoteEntity::class, StudyDayEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DevFocusConverters::class)
abstract class DevFocusDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
    abstract fun studyDao(): StudyDao

    companion object {
        @Volatile
        private var INSTANCE: DevFocusDatabase? = null

        fun getDatabase(context: Context): DevFocusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DevFocusDatabase::class.java,
                    "devfocus_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class DevFocusConverters {
    @TypeConverter
    fun fromStudyStatus(status: StudyStatus): String = status.name

    @TypeConverter
    fun toStudyStatus(status: String): StudyStatus = StudyStatus.valueOf(status)
}
