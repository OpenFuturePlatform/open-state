package io.openfuture.state.controller

import io.openfuture.state.controller.domain.dto.OpenScaffoldDto
import io.openfuture.state.controller.domain.request.SaveOpenScaffoldRequest
import io.openfuture.state.service.OpenScaffoldService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/open-scaffolds")
class OpenScaffoldController(
        private val openScaffoldService: OpenScaffoldService
) {

    @PostMapping
    suspend fun save(@RequestBody @Valid request: SaveOpenScaffoldRequest): OpenScaffoldDto {
        return OpenScaffoldDto(openScaffoldService.save(request))
    }

}
