package org.personal.crypto_watcher.spark;



public class SparkLib {

/*    public SQLContext sqlContext;

    public SparkLib(){

        SparkConf conf = new SparkConf();
        conf.setAppName("Spark");
        conf.setMaster("local");
        SparkContext sparkContext = new SparkContext(conf);
        this.sqlContext = new SQLContext(sparkContext);
    }

    public Dataset<Row> createDF(String csvLoc){

        return sqlContext.read().format("csv")
                .option("header","true")
                .load(csvLoc);

    }

    public void writeDF(Dataset<Row> df, String csvLoc){

        df.write().format("csv")
                .option("header","true")
                .save(csvLoc);

    }*/
}
