package cm.study.vertx.database;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * Created by chenming on 2017/10/6.
 */
@ProxyGen
public interface WikiDatabaseService {

    static WikiDatabaseService create() {
        return new WikiDatabaseServiceImpl();
    }

    @Fluent
    WikiDatabaseService fetchAllPages(Handler<AsyncResult<JsonArray>> resultHandler);

}
