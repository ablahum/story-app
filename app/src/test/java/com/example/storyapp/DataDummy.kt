package com.example.storyapp

import com.example.storyapp.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyItemResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "2023-12-14T13:53:28.833Z",
                "Tama",
                "Aku mencintaimu dalam diam",
                106.83332584798336,
                "1",
                -6.300554389676513,
            )
            items.add(story)
        }
        return items
    }
}