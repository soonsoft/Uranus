package com.soonsoft.uranus.services.membership.batch;

import java.util.UUID;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.service.mybatis.MybatisDatabaseAccess;
import com.soonsoft.uranus.services.membership.config.MembershipConfig;
import com.soonsoft.uranus.services.membership.po.AuthPrivilege;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {MembershipConfig.class})
public class MybatisBatchTest {

    @Autowired
    @Qualifier("membershipAccess")
    private IDatabaseAccess<?> databaseAccess;

    @Test
    public void test_normalInsert() {
        long startTime = System.currentTimeMillis();

        int effectRows = 0;
        for(int i = 0; i < 100000; i++) {
            AuthPrivilege privilege = new AuthPrivilege();
            privilege.setUserId(UUID.randomUUID());
            privilege.setFunctionId(UUID.randomUUID());
            effectRows += databaseAccess.insert("uranus.auth_privilege.insert", privilege);
        }

        Assert.assertTrue(effectRows == 100000);

        // 耗时：66435
        System.out.println("耗时：" + (System.currentTimeMillis() - startTime));

    }

    @Test
    public void test_batchInsert() {
        long startTime = System.currentTimeMillis();

        int effectRows = 0;

        SqlSessionFactory sessionFactory = ((MybatisDatabaseAccess) databaseAccess).getTemplate().getSqlSessionFactory();
        try(SqlSession session = sessionFactory.openSession(ExecutorType.BATCH)) {
            for(int i = 0; i < 100000; i++) {
                AuthPrivilege privilege = new AuthPrivilege();
                privilege.setUserId(UUID.randomUUID());
                privilege.setFunctionId(UUID.randomUUID());
                session.insert("uranus.auth_privilege.insert", privilege);
            }

            session.commit();
            effectRows = 100000;
        }

        Assert.assertTrue(effectRows == 100000);

        // 耗时：9266
        System.out.println("耗时：" + (System.currentTimeMillis() - startTime));
    }
    
}
