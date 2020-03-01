package life.jiasheng.community.mapper;

import life.jiasheng.community.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

//@Component
//@Repository
@Mapper
public interface UserMappper {

    @Insert("insert into user(name,account_id,token,gmt_create,gmt_modified) values(#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified})")
    public void insert(User user);
}
