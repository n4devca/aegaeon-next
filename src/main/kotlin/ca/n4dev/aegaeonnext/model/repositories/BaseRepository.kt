package ca.n4dev.aegaeonnext.model.repositories

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

/**
 *
 * BaseRepository.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 22 - 2019
 *
 */
abstract class BaseRepository {

    @Autowired
    protected lateinit var jdbcTemplate: NamedParameterJdbcTemplate;

    protected fun params(vararg params : Any) : Map<String, Any> {
        require(params.size % 2 == 0) {
            "The parameters should be pairs."
        }

        val map : MutableMap<String, Any> = LinkedHashMap();
        for (i in params.indices step 2) {
            map.put(params[i] as String, params[i + 1])
        }

        return map
    }
}