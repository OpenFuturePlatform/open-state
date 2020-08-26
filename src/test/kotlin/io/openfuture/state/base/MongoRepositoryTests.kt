package io.openfuture.state.base

import io.openfuture.state.repository.MongoIndexCreator
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.MongoConverter

@DataMongoTest(excludeAutoConfiguration = [EmbeddedMongoAutoConfiguration::class])
abstract class MongoRepositoryTests {

    @Autowired
    protected lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @Autowired
    protected lateinit var mongoConverter: MongoConverter

    @BeforeEach
    open fun setUp() {
        val indexCreator = MongoIndexCreator(mongoConverter, reactiveMongoTemplate)
        indexCreator.initIndicesAfterStartup()
    }

}
