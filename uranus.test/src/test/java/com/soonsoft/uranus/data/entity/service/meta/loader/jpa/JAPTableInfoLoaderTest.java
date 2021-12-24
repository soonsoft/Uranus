package com.soonsoft.uranus.data.entity.service.meta.loader.jpa;

import java.sql.JDBCType;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.soonsoft.uranus.data.service.meta.IDType;
import com.soonsoft.uranus.data.service.meta.TableInfo;
import com.soonsoft.uranus.data.service.meta.anno.MappingType;
import com.soonsoft.uranus.data.service.meta.loader.ITableInfoLoader;
import com.soonsoft.uranus.data.service.meta.loader.jpa.JAPTableInfoLoader;

import org.junit.Assert;
import org.junit.Test;

public class JAPTableInfoLoaderTest {

    private ITableInfoLoader loader = new JAPTableInfoLoader();

    @Table(name = "table_rel")
    public static class TableRel {

        @Id
        @Column(name = "p1")
        @MappingType(JDBCType.VARCHAR)
        private Integer id1;

        @Id
        @Column(name = "p2")
        private int id2;
    }

    @Table(name = "table_auto_id")
    public static class TableAutoId {

        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.AUTO)
        private int id;

    }

    @Table(name = "table1")
    public static class Table1 {
        
        @Id
        @Column(name = "id1")
        private UUID id1;
    }

    @Table(name = "table2")
    public static class Table2 extends Table1 {

        @Id
        @Column(name = "id2")
        private int id2;
    }

    @Table(name = "table3")
    public static class Table3 extends Table2 {

        @Id
        @Column(name = "id3")
        private String id3;
    }

    @Test
    public void test_Extends() {
        TableInfo tableInfo = loader.load(Table3.class);

        Assert.assertTrue(tableInfo.getPrimaryKey().isComposite());
        Assert.assertTrue(tableInfo.getPrimaryKey().getKeys().size() == 3);
        Assert.assertTrue(tableInfo.getColumns().size() == 3);
        Assert.assertTrue(tableInfo.getColumns().get(0).getColumnName().equals("id1"));
        Assert.assertTrue(tableInfo.getColumns().get(2).getColumnName().equals("id3"));
    }

    @Test
    public void test_Composite() {
        TableInfo tableInfo = loader.load(TableRel.class);

        Assert.assertTrue(tableInfo.getPrimaryKey().isComposite());
        Assert.assertTrue(tableInfo.getPrimaryKey().getKeys().size() == 2);
        Assert.assertTrue(tableInfo.getColumns().get(0).getColumnName().equals("p1"));
        Assert.assertTrue(tableInfo.getColumns().get(0).getColumnType() == JDBCType.VARCHAR);
        Assert.assertTrue(tableInfo.getColumns().get(1).getColumnType() == JDBCType.INTEGER);
        Assert.assertTrue(tableInfo.getPrimaryIdType() == IDType.NONE);
    }

    @Test
    public void test_AutoId() {
        TableInfo tableInfo = loader.load(TableAutoId.class);

        Assert.assertTrue(tableInfo.getTableName().equals("table_auto_id"));
        Assert.assertTrue(tableInfo.getPrimaryIdType() == IDType.AUTO);
        Assert.assertTrue(!tableInfo.getPrimaryKey().isComposite());
        Assert.assertTrue(!tableInfo.getColumns().get(0).isNotNull());
    }
    
}
