package io.openfuture.state.domain.page

import org.springframework.data.domain.Page

data class PageResponse<T>(var totalCount: Long, var list: List<T>) {

    constructor(page: Page<T>) : this(page.totalElements, page.content)

}
