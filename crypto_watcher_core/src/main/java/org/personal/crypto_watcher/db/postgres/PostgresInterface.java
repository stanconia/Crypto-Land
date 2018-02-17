package org.personal.crypto_watcher.db.postgres;


import org.joda.time.DateTime;
import org.personal.crypto_watcher.controller.mrkt_cap.MrktCapMgr;
import org.personal.crypto_watcher.db.DBInterface;
import org.personal.crypto_watcher.model.MarketCap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PostgresInterface  {

/*    private static SessionFactory factory;
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresInterface.class);

    static{
        setup();
    }

    public static MarketCap getMarketCap(DateTime time){
        Session session = factory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            List employees = session.createQuery("from MarketCap where time = " + "'" +time.toString("MM/dd/yyyy HH:mm") + "'").list();
            tx.commit();
            if (employees.isEmpty()){
                return MrktCapMgr.getEmptyObj(time);
            }else{
                MarketCap marketCap = (MarketCap) employees.get(0);
                return marketCap;
            }
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            return null;
        }finally {
            session.close();
        }
    }

    public static MarketCap getLatestMarketCap(){

        Session session = factory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            List employees = session.createQuery("from MarketCap ORDER BY ID DESC").list();
            tx.commit();
            if (employees.isEmpty()){
                return MrktCapMgr.getEmptyObj(new DateTime());
            }else{
                MarketCap marketCap = (MarketCap) employees.get(0);
                return marketCap;
            }
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            return null;
        }finally {
            session.clear();
            session.close();
        }
    }

    public static void putMarketCap(MarketCap mrktCap){

        Session session = factory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            session.save(mrktCap);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            System.out.println(e.getMessage());
        }finally {
            session.clear();
            session.close();
            //factory.close();
        }
    }

    private static void setup(){

        Configuration conf = new Configuration().
                configure().
                        addAnnotatedClass(MarketCap.class);

        String env = System.getProperty("env");

        String dbName = "test";
        String userName = "nav749";
        String password = "odaliki123";
        String hostname = "localhost";
        String port = "5432";

        if(env ==  null){
            dbName = "testdb";
            userName = System.getenv("RDS_USERNAME");
            password = System.getenv("RDS_PASSWORD");
            hostname = System.getenv("RDS_HOSTNAME");
            port = System.getenv("RDS_PORT");
        }

        conf.setProperty("hibernate.connection.url","jdbc:postgresql://" + hostname + ":" + port + "/" + dbName);
        conf.setProperty("hibernate.connection.username",userName);
        conf.setProperty("hibernate.connection.password",password);
        factory = conf.buildSessionFactory();
    }*/

}
