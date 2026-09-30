package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.BloodBankDao
import com.example.data.dao.DonationRecordDao
import com.example.data.dao.DonorDao
import com.example.data.dao.EmergencyRequestDao
import com.example.data.dao.StockBatchDao
import com.example.data.entity.BloodBankEntity
import com.example.data.entity.DonationRecordEntity
import com.example.data.entity.DonorEntity
import com.example.data.entity.EmergencyRequestEntity
import com.example.data.entity.StockBatchEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BloodBankEntity::class,
        DonorEntity::class,
        EmergencyRequestEntity::class,
        DonationRecordEntity::class,
        StockBatchEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LifeLinkDatabase : RoomDatabase() {

    abstract fun bloodBankDao(): BloodBankDao
    abstract fun donorDao(): DonorDao
    abstract fun emergencyRequestDao(): EmergencyRequestDao
    abstract fun donationRecordDao(): DonationRecordDao
    abstract fun stockBatchDao(): StockBatchDao

    companion object {
        @Volatile
        private var INSTANCE: LifeLinkDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): LifeLinkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeLinkDatabase::class.java,
                    "lifelink_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        scope.launch(Dispatchers.IO) {
                            INSTANCE?.let { database ->
                                InitialData.populateDatabase(database)
                            }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
