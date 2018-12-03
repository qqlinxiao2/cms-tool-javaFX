package sample;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface OracleMapper {

    @Select("select value from T_CMS_CONTENT_EXT where id=#{id} and name=#{name}")
    String queryContentExtValue(@Param("id") String id, @Param("name") String name);

    @Select("select value from T_CMS_CONTENT_EXT where name=#{name} and value=#{value}")
    String queryContentIdByMapping(@Param("name")String name, @Param("value") String codeMapping);

    @Select("select provider_code from T_CMS_CONTENT where id=#{id}")
    String queryProviderById(@Param("id") String id);

    @Select("select provider_code from T_CMS_CONTENT where code=#{code}")
    String queryProviderByCode(@Param("code") String code);
}
