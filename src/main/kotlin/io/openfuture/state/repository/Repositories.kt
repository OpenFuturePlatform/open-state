package io.openfuture.state.repository

import io.openfuture.state.entity.BaseModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface BaseRepository<T : BaseModel> : JpaRepository<T, Long>