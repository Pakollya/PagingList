package com.pakollya.paginglist

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

interface DaysRepository {

    fun dayMillis(): Long
    fun dateToStartDay(date: Long): Long
    suspend fun dateStringByIndex(index: Int): String
    suspend fun dateMillisByIndex(index: Int): Long
    suspend fun indexByDate(date: Long): Int
    suspend fun daysCount(): Int
    suspend fun parseMessages(messages: List<Message.Data>)

    class Base(
        private val dao: DaysDao
    ) : DaysRepository {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        override fun dayMillis(): Long = 1000*60*60*24

        override fun dateToStartDay(date: Long): Long {
            val dateTemp = dateFormat.format(Date(date))
            return dateFormat.parse(dateTemp).time
        }

        override suspend fun dateStringByIndex(index: Int): String {
            val day = dao.dayByIndex(index)
            return dateFormat.format(Date(day))
        }

        override suspend fun dateMillisByIndex(index: Int) = dao.dayByIndex(index)

        override suspend fun indexByDate(date: Long) = dao.indexByDate(date)

        override suspend fun daysCount() = dao.daysCount()

        override suspend fun parseMessages(messages: List<Message.Data>) {
            val days = mutableListOf<Day>()
            val now = System.currentTimeMillis()
            val stamp = messages[0].timestamp
            val startDayInMillis = dateToStartDay(stamp)

            var currentPage = 0
            var pageSize = 0
            var startPosition = 0
            var currentDate = startDayInMillis
            var counter = 0

            for (i in startDayInMillis .. now step dayMillis()) {

                pageSize = messages.count {
                    currentDate <= it.timestamp &&
                            it.timestamp < (currentDate + dayMillis())
                }

                Log.d("For counter", "$counter")
                Log.d("For pageSize", "$pageSize")

                if (pageSize > 0) {
                    val day = Day(currentPage, pageSize, startPosition, currentDate)
                    Log.d("For DAY", day.toString())
                    days.add(day)
                }

                counter++
                startPosition += (pageSize + 1)
                currentPage++
                currentDate += dayMillis()
                pageSize = 0
            }

            if (days.isNotEmpty())
                dao.insertDays(days)
        }
    }
}