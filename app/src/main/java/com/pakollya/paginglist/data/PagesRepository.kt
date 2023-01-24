package com.pakollya.paginglist.data

import com.pakollya.paginglist.presentation.MessagesPageUi
import com.pakollya.paginglist.data.MessagesRepository.Strategy
import com.pakollya.paginglist.data.cache.day.DayPart
import com.pakollya.paginglist.data.cache.day.DayPartsDao
import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.data.cache.message.MessagesDao
import com.pakollya.paginglist.data.cache.page.Page
import com.pakollya.paginglist.data.cache.page.PagesDao
import java.text.SimpleDateFormat
import java.util.*

interface PagesRepository {

    fun pageByStrategy(strategy: Strategy): Int
    fun pageCount(): Int
    fun pageSize(): Int
    fun currentPages(): List<Int>
    fun currentPagesUi(): List<MessagesPageUi>
    fun isLastPage(): Boolean
    fun isFirstPage(): Boolean

    fun dayMillis(): Long
    fun dateStringFromMillis(date: Long): String
    fun dateToStartDay(date: Long): Long

    fun updatePageCount(listCount: Int)
    fun updatePage(pageIndex: Int): Boolean
    fun pageIndexById(id: Long): Int
    fun updateCurrentPageUi(page: MessagesPageUi, strategy: Strategy)

    suspend fun setLastPage()
    suspend fun dayPartsByPageIndex(pageIndex: Int): List<DayPart>
    suspend fun dayPartById(id: Long): DayPart
    suspend fun parseDayParts(messages: List<Message.Data>, pageIndex: Int)
    suspend fun lastPositionById(lastId: Long): Int

    fun currentPageMessages(
        messages: List<Message.Data>,
        pageIndex: Int,
    ): List<Message.Data>

    fun messagesToPage(
        messages: List<Message.Data>,
        pageIndex: Int,
    ): Page

    suspend fun parseAllMessages(
        messages: List<Message.Data>,
    )

    class Base(
        private val messagesDao: MessagesDao,
        private val dayPartsDao: DayPartsDao,
        private val pagesDao: PagesDao,
        private val pageSize: Int = 100
    ) : PagesRepository {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        //TODO: add pageCount and page storage
        private val currentPages = mutableListOf(0)
        private val currentPagesUi = mutableListOf<MessagesPageUi>()
        private var pageCount = 0

        override fun currentPages(): List<Int> {
            val pages = mutableListOf<Int>()
            pages.addAll(currentPages)
            return pages
        }

        override fun currentPagesUi(): List<MessagesPageUi> {
            val pages = mutableListOf<MessagesPageUi>()
            pages.addAll(currentPagesUi)
            return pages
        }

        override fun isLastPage(): Boolean {
            currentPages.forEach {
                if (it == pageCount)
                    return true
            }
            return false
        }

        override fun isFirstPage(): Boolean {
            currentPages.forEach {
                if (it == 0)
                    return true
            }
            return false
        }

        override fun updateCurrentPageUi(page: MessagesPageUi, strategy: Strategy) {
            when (strategy) {
                Strategy.NEXT -> {
                    currentPagesUi.forEach {
                        if (it.pageIndex == pageCount) return
                    }
                    if (currentPagesUi.size >= 2) {
                        currentPagesUi.removeAt(0)
                    }
                    currentPagesUi.add(page)
                }
                Strategy.PREVIOUS -> {
                    currentPagesUi.forEach {
                        if (it.pageIndex == 0) return
                    }
                    if (currentPagesUi.size >= 2) {
                        currentPagesUi.removeAt(1)
                    }
                    currentPagesUi.add(0, page)
                }
                Strategy.INIT -> {
                    currentPagesUi.clear()
                    currentPagesUi.add(page)
                }
            }
        }

        override fun pageByStrategy(strategy: Strategy): Int {
            when (strategy) {
                Strategy.NEXT -> {
                    currentPages.forEach {
                        if (it == pageCount){
                            return it
                        }
                    }

                    if (currentPages.size >= 2) {
                        currentPages.removeAt(0)
                    }

                    currentPages.add(currentPages[0] + 1)
                    return currentPages[1]
                }
                Strategy.PREVIOUS -> {
                    currentPages.forEach { page ->
                        if (page == 0) return 0
                    }

                    if (currentPages.size >= 2) {
                        currentPages.removeAt(1)
                    }

                    currentPages.add(0, currentPages[0] - 1)
                    return currentPages[0]
                }
                Strategy.INIT -> {
                    return currentPages[0]
                }
            }
        }

        override fun pageCount() = pageCount
        override fun pageSize() = pageSize

        override fun dayMillis(): Long = 1000 * 60 * 60 * 24

        override fun dateStringFromMillis(date: Long): String {
            return dateFormat.format(Date(date))
        }

        override fun dateToStartDay(date: Long): Long {
            val dateTemp = dateFormat.format(Date(date))
            return dateFormat.parse(dateTemp).time
        }

        override fun updatePageCount(listCount: Int) {
            pageCount = listCount / pageSize
        }

        override fun updatePage(pageIndex: Int): Boolean {
            currentPagesUi.forEach{
                if (it.pageIndex == pageIndex) {
                    return false
                }
            }

            currentPages.clear()
            currentPages.add(pageIndex)

            return true
        }

        override fun pageIndexById(id: Long): Int {
            return pagesDao.pageIndexById(id)
        }

        override suspend fun setLastPage() {
            currentPages.clear()
            currentPages.add(pageCount)
        }

        /**
         * получаем список всех сообщений
         * по индексу страницы отдаем список сообщений для данной стриницы
         **/
        override fun currentPageMessages(
            messages: List<Message.Data>,
            pageIndex: Int,
        ): List<Message.Data> {
            var listMessage = listOf<Message.Data>()
            //проверяем последняя страница или нет, т.к. может быть не полной
            if (pageIndex == pageCount) {
                //получаем размер последней стриницы
                val lastPageSize = messages.count() - pageSize * pageCount
                if (lastPageSize > 0) {
                    listMessage = messages
                        .slice(pageIndex * pageSize until (pageIndex * pageSize + lastPageSize))
                }
            } else {
                listMessage = messages
                    .slice(pageIndex * pageSize until (pageIndex + 1) * pageSize)
            }
            return listMessage
        }

        /**
         * Получаем список собщений для одной из стриниц
         * отдаем page
         **/
        override fun messagesToPage(
            messages: List<Message.Data>,
            pageIndex: Int,
        ): Page {
            return Page(
                index = pageIndex,
                startId = messages[0].messageId(),
                endId = messages.last().messageId()
            )
        }

        /**
         * Получаем список всех сообщений
         * список сообщений разделяем на страницы
         **/
        override suspend fun parseAllMessages(
            messages: List<Message.Data>,
        ) {
            val pageList = mutableListOf<Page>()

            //пробегаемся по всем стриницам: делим список сообщений на стриницы, а страницы на дни
            for (pageIndex in 0..pageCount) {

                //получаем соообщения для одной страницы
                val messages = currentPageMessages(
                    messages = messages,
                    pageIndex = pageIndex
                )

                //сохраняем данные о старнице
                val page = messagesToPage(
                    messages = messages,
                    pageIndex = pageIndex
                )
                pageList.add(page)

                //список сообщений одной стриницы делим на дни
                parseDayParts(
                    messages = messages,
                    pageIndex = pageIndex
                )
            }

            if (pageList.isNotEmpty()) {
                pagesDao.deletePages()
                pagesDao.insertPages(pageList)
            }
        }

        /**
         * По индексу страницы получаем все дни (части дней) для данной стриницы
         **/
        override suspend fun dayPartsByPageIndex(pageIndex: Int): List<DayPart> {
            return dayPartsDao.dayPartsByPage(pageIndex)
        }

        /**
         * По id сообщения получаем день (часть дня) в котором он находится
         **/
        override suspend fun dayPartById(id: Long) = dayPartsDao.dayPartById(id)

        /**
         * По последнему id сообщения получаем последнюю позицию на странице
         **/
        override suspend fun lastPositionById(lastId: Long) = dayPartsDao.lastPositionById(lastId)

        /**
         * Получаем список сообщений для одной страницы
         * список сообщений разделяем на дни (день может быть не полным, имеются ввиду кусочки дня)
         **/
        override suspend fun parseDayParts(messages: List<Message.Data>, pageIndex: Int) {
            val dayParts = mutableListOf<DayPart>()
            val now = System.currentTimeMillis()
            //получаем самую первую дату (самую старую)
            val stamp = messages[0].timestamp
            //из самого первого дня получаем начало дня по 00:00:00
            val startDayInMillis = dateToStartDay(stamp)

            var currentPage = 0
            var partSize = 0
            var startPosition = 0
            var currentDate = startDayInMillis
            var counter = 0
            var messagesPerDay: List<Message>

            //учет кнопки previous
            if (pageIndex != 0) {
                startPosition += 1
            }

            //пробегаемся по всем дням начиная с самого первого (старого) с шагом в один день
            for (day in startDayInMillis..now step dayMillis()) {

                //находим все сообщения из списка за этот день
                messagesPerDay = messages.filter {
                    currentDate <= it.timestamp &&
                            it.timestamp < (currentDate + dayMillis())
                }

                //количество сообщений за день
                partSize = messagesPerDay.size

                if (partSize > 0) {
                    //находим абсолютно все сообщения за данный день из всей базы
                    val listMessage = messagesDao.messages(
                        startDate = currentDate,
                        endDate = currentDate + dayMillis()
                    )

                    //сравниваем первый элемент из всей базы и из нашего списка
                    //если первые элементы совпадают, то значит это начало дня и нужно добавлять header
                    //если не совпадают, значит нам попался остаток дня и header НЕ нужен
                    val reminder = listMessage[0] != messagesPerDay[0]

                    //если начало дня, то учитываем, что есть Header
                    if (!reminder) {
                        startPosition += 1
                    }

                    //формируем частичку дня
                    val partDay = DayPart(
                        pageIndex = pageIndex,
                        startId = messagesPerDay[0].messageId(),
                        endId = messagesPerDay.last().messageId(),
                        reminder = reminder,
                        startPosition = startPosition,
                        endPosition = (startPosition + partSize - 1),
                        date = currentDate
                    )

                    dayParts.add(partDay)
                }

                //переходим на следующий день
                counter++
                startPosition += partSize
                currentPage++
                currentDate += dayMillis()
                partSize = 0
            }

            //TODO: временная очистка кэша, позже убрать
            if (pageIndex == 0) {
                dayPartsDao.deleteDayParts()
            }

            //сохраняем все кусочки дней
            if (dayParts.isNotEmpty()) {
                dayPartsDao.deletePartsByPage(pageIndex)
                dayPartsDao.insertDayParts(dayParts)
            }
        }
    }
}