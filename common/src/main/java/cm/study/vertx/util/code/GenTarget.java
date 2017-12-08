package cm.study.vertx.util.code;

/**
 * 代码生成的目标类型
 */
public enum GenTarget {

    DOMAIN,             // 由实体生成dao, map, xml一系统
    DAO_UNIT_TEST,      // 由dao生成单元测试
    SERVICE_UNIT_TEST,  // 由Service生成单元测试
    ;
}
