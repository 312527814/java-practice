package com.my;

import com.my.mapper.userMapper;
import com.my.pojo.user;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.jdbc.ConnectionLogger;
import org.apache.ibatis.session.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.my.pojo.flower;
import com.my.mapper.flowerMapper;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

import javax.sql.DataSource;

public class MybaitsApp {

    public static void main(String[] args) throws IOException {
        // ��ȡmybatis-config.xml�ļ�
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
//        DefaultSqlSessionFactory

        // ��ʼ��mybatis������SqlSessionFactory���ʵ��
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession session = sqlSessionFactory.openSession();

        flowerMapper mapper = session.getMapper(flowerMapper.class);
//        flower flower = mapper.selectById(10);
//        flower.get_user();
        //表示从第几条数据开始
        int offset = 0;
        //连续取出几条数据
        int limit = 5;

        RowBounds rowBounds = new RowBounds(offset, limit);
        List<flower> flowers = mapper.selectAll(rowBounds);

    }
    public static void main1(String[] args) throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession session = sqlSessionFactory.openSession(false);
        session.commit();
        SqlSession session2 = sqlSessionFactory.openSession();
        flowerMapper mapper = session.getMapper(flowerMapper.class);
        mapper.updateById(2010);
        PooledDataSource dataSource = (PooledDataSource) session.getConfiguration().getEnvironment().getDataSource();
        int activeConnectionCount = dataSource.getPoolState().getActiveConnectionCount();
        int idleConnectionCount = dataSource.getPoolState().getIdleConnectionCount();

    }
    public static void main2(String[] args) throws IOException, SQLException, InterruptedException {

//        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

        // ��ȡmybatis-config.xml�ļ�
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
//        DefaultSqlSessionFactory

        // ��ʼ��mybatis������SqlSessionFactory���ʵ��
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        // org.apache.ibatis.datasource.pooled.PooledDataSourceFactory
//        Configuration configuration = sqlSessionFactory.getConfiguration();

        // ����Sessionʵ��
        AtomicInteger i = new AtomicInteger(0);
        for (i.get(); i.get() < 20; i.getAndIncrement()) {


            Thread thread = new Thread(() -> {

                SqlSession session = sqlSessionFactory.openSession();
                try {
                    session.getConnection().setAutoCommit(false);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1 + i.get() * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                flowerMapper mapper = session.getMapper(flowerMapper.class);
                mapper.updateById(2010);
                PooledDataSource dataSource = (PooledDataSource) session.getConfiguration().getEnvironment().getDataSource();
                int activeConnectionCount = dataSource.getPoolState().getActiveConnectionCount();
                int idleConnectionCount = dataSource.getPoolState().getIdleConnectionCount();

                session.commit();
                // session.getConnection().close();
                session.close();

                try {
                    Thread.sleep(1 + i.get() * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.print("��ǰ��Ծ���߳�����" + activeConnectionCount + ",��ǰ���õ��߳�����" + idleConnectionCount +","+ i.get()+"\r\n");


            });
            thread.start();
        }
//        SqlSession session = sqlSessionFactory.openSession();
//        SqlSession session2 = sqlSessionFactory.openSession();
//
////        Object o = session.selectOne("com.my.mapper.flowerMapper.selectById", 2010);
//
//        flowerMapper mapper = session.getMapper(flowerMapper.class);
//        flower flower = mapper.selectById(2010);
//        List<com.my.pojo.flower> flowers = mapper.selectAll();
//
//        int i = mapper.updateById(2010);
//        System.out.print(flower);


        //Thread.sleep(4000);

        System.in.read();

    }
}
