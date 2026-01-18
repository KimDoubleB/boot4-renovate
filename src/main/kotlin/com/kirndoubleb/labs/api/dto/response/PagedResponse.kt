package com.kirndoubleb.labs.api.dto.response

import org.springframework.data.domain.Page

data class PagedResponse<T : Any>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean
) {
    companion object {
        fun <T : Any, R : Any> from(page: Page<T>, mapper: (T) -> R) = PagedResponse(
            content = page.content.map(mapper),
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            first = page.isFirst,
            last = page.isLast
        )
    }
}
