package com.pakollya.paginglist

import android.util.Log
import com.pakollya.paginglist.MessagesRepository.Strategy.*
import java.util.Random

interface MessagesRepository {

    enum class Strategy {
        INIT,
        NEXT,
        PREVIOUS
    }

    fun init()

    fun messages(strategy: Strategy = INIT): List<Message>

    fun changePage(id: Int): Boolean

    fun positionOnPageById(id: Int): Int

    fun addMessage()

    fun setLastPage()

    fun randomId(): Int

    class Base(
        private val cache: MessageCache = DependencyContainer.Base.provideCache()
    ): MessagesRepository {
        private var page = 0

        override fun init() {
            cache.init()
        }

        /**
         * Отдаем список сообщений для одной страницы в зависимости от стратегии
         **/
        override fun messages(strategy: Strategy): List<Message> {
            //Стратегия: двигаемся на страницу вперед или назад
            if (strategy == NEXT) {
                page++
            } else if (strategy == PREVIOUS) {
                page--
            }

            //Получаем все сообщения и создаем новый список для отображения
            val allMessages = cache.messages()
            val list = mutableListOf<Message>()

            //если НЕ первая страница, то добавляем возможность вернуться на предыдущую страницу
            if (page > 0) {
                list.add(Message.Previous)
            }

            //Добавляем границу для последней страницы, т.к. ее размер может быть < PAGE_SIZE
            var bound = PAGE_SIZE
            if (page + 1 == MAXIMUM_PAGES) {
                bound = allMessages.last().id.toInt() - (page*PAGE_SIZE) + 1
            }
            Log.d("Bound", "$bound")

            //Добавляем в список сообщения для страницы page
            for (i in 0 until bound) {
                list.add(allMessages[(page* PAGE_SIZE) + i])
            }

            //если НЕ последняя страница, то добавляем возможность перейти на следующую страницу
            if (page + 1 < MAXIMUM_PAGES) {
                list.add(Message.Next)
            }

            return list
        }

        /**
         * Меняем значение страницы на новый по id элемента
         **/
        override fun changePage(id: Int): Boolean {
            //Находим страницу на которой расположен элемент
            val itemPage = id/PAGE_SIZE
            Log.d("OldPage ", "$page")
            Log.d("NewPage ", "$itemPage")

            //проверяем находимся ли мы сейчас на данной странице или на другой
            return if (itemPage == page){
                false
            } else {
                page = itemPage
                true
            }
        }

        override fun setLastPage() {
            page = MAXIMUM_PAGES - 1
        }

        /**
         * Вычисляем позицию элемента в списке по id (для RecyclerView)
         **/
        override fun positionOnPageById(id: Int): Int {
            Log.d("id ", "$id")
            Log.d("page*PAGE_SIZE ", "${page*PAGE_SIZE}")
            Log.d("position ", "${id - page*PAGE_SIZE + 1}")
            return id - page*PAGE_SIZE + 1
        }

        override fun addMessage() {
            cache.addMessage()
        }

        override fun randomId(): Int = Random().nextInt(cache.count())

        companion object {
            private const val PAGE_SIZE = 100
            private const val MAXIMUM_PAGES = 4
        }
    }
}