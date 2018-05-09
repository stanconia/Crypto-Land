package org.personal.crypto_watcher.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import org.personal.crypto_watcher.spark.SparkLib;

import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;



public class DBMigrator {


    /*public void copyCSVToPostgres(){

        String symbols = "BTC-1ST,BTC-2GIVE,BTC-ABY,BTC-ADA,BTC-ADT,BTC-ADX,BTC-AEON,BTC-AGRS,BTC-AMP,BTC-ANT,BTC-ARDR,BTC-ARK,BTC-AUR,BTC-BAT,BTC-BAY,BTC-BCC,BTC-BCPT,BTC-BCY,BTC-BITB,BTC-BLITZ,BTC-BLK,BTC-BLOCK,BTC-BNT,BTC-BRK,BTC-BRX,BTC-BSD,BTC-BTG,BTC-BURST,BTC-BYC,BTC-CANN,BTC-CFI,BTC-CLAM,BTC-CLOAK,BTC-CLUB,BTC-COVAL,BTC-CPC,BTC-CRB,BTC-CRW,BTC-CURE,BTC-CVC,BTC-DASH,BTC-DCR,BTC-DCT,BTC-DGB,BTC-DMD,BTC-DNT,BTC-DOGE,BTC-DOPE,BTC-DTB,BTC-DYN,BTC-EBST,BTC-EDG,BTC-EFL,BTC-EGC,BTC-EMC,BTC-EMC2,BTC-ENG,BTC-ENRG,BTC-ERC,BTC-ETC,BTC-ETH,BTC-EXCL,BTC-EXP,BTC-FAIR,BTC-FCT,BTC-FLDC,BTC-FLO,BTC-FTC,BTC-GAM,BTC-GAME,BTC-GBG,BTC-GBYTE,BTC-GCR,BTC-GEO,BTC-GLD,BTC-GNO,BTC-GNT,BTC-GOLOS,BTC-GRC,BTC-GRS,BTC-GUP,BTC-HMQ,BTC-IGNIS,BTC-INCNT,BTC-INFX,BTC-IOC,BTC-ION,BTC-IOP,BTC-KMD,BTC-KORE,BTC-LBC,BTC-LGD,BTC-LMC,BTC-LRC,BTC-LSK,BTC-LTC,BTC-LUN,BTC-MAID,BTC-MANA,BTC-MCO,BTC-MEME,BTC-MER,BTC-MLN,BTC-MONA,BTC-MUE,BTC-MUSIC,BTC-NAV,BTC-NBT,BTC-NEO,BTC-NEOS,BTC-NLG,BTC-NMR,BTC-NXC,BTC-NXS,BTC-NXT,BTC-OK,BTC-OMG,BTC-OMNI,BTC-PART,BTC-PAY,BTC-PDC,BTC-PINK,BTC-PIVX,BTC-PKB,BTC-POT,BTC-POWR,BTC-PPC,BTC-PTC,BTC-PTOY,BTC-QRL,BTC-QTUM,BTC-QWARK,BTC-RADS,BTC-RBY,BTC-RCN,BTC-RDD,BTC-REP,BTC-RLC,BTC-SALT,BTC-SBD,BTC-SC,BTC-SEQ,BTC-SHIFT,BTC-SIB,BTC-SLR,BTC-SLS,BTC-SNRG,BTC-SNT,BTC-SPHR,BTC-SPR,BTC-SRN,BTC-START,BTC-STEEM,BTC-STORJ,BTC-STRAT,BTC-SWIFT,BTC-SWT,BTC-SYNX,BTC-SYS,BTC-THC,BTC-TIX,BTC-TKS,BTC-TRST,BTC-TRUST,BTC-TRX,BTC-TUSD,BTC-TX,BTC-UBQ,BTC-UKG,BTC-UNB,BTC-VEE,BTC-VIA,BTC-VIB,BTC-VOX,BTC-VRC,BTC-VRM,BTC-VTC,BTC-VTR,BTC-WAVES,BTC-WAX,BTC-WINGS,BTC-XCP,BTC-XDN,BTC-XEL,BTC-XEM,BTC-XLM,BTC-XMG,BTC-XMR,BTC-XMY,BTC-XRP,BTC-XST,BTC-XVC,BTC-XVG,BTC-XWC,BTC-XZC,BTC-ZCL,BTC-ZEC,BTC-ZEN,BTC-ZRX,USDT-ADA,USDT-BCC,USDT-BTC,USDT-BTG,USDT-DASH,USDT-ETC,USDT-ETH,USDT-LTC,USDT-NEO,USDT-NXT,USDT-OMG,USDT-XMR,USDT-XRP,USDT-XVG,USDT-ZEC";
        //Arrays.asList(symbols.split(",")).forEach(symbol -> deleteData(symbol));
        SparkLib lib = new SparkLib();
        Dataset<Row> df = getDF(lib);
        UDF1 udf =new UDF1<String, String>() {
            @Override
            public String call(String id) {
                return id.split("_")[1];
            }
        };
        lib.sqlContext.udf().register("CTO", udf, DataTypes.StringType);
        Arrays.asList(symbols.split(",")).forEach(symbol -> {
            Dataset<Row> symbolDF = df.filter(col("symbol").equalTo(symbol));
            Dataset<Row> newDF = symbolDF.withColumn("time", callUDF("CTO",col("_id")));
            Dataset<Row> newestDF =newDF.drop(col("_id")).drop(col("symbol"));
            writeToPostgres(newestDF,symbol);
            //lib.writeDF(newestDF,"/Users/stanleyopara/software/data_symbols/" + symbol);
        });
    }

    private void writeToPostgres(Dataset<Row> newestDF, String symbol) {

        String driver = "org.postgresql.Driver";
        String host = "crypto.carya3oyik21.us-east-1.rds.amazonaws.com";
        String port = "5432";
        String dbName = "data";
        String user = "stanwizzy";
        String pswd = "odaliki123";
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
        String connectStr = url + "?user=" + user + "&password=" + pswd;



        newestDF.write().option("driver",driver)
                .mode(SaveMode.Append)
                .jdbc(connectStr,"\"" + symbol + "\"",new Properties());
    }

    private  Dataset<Row> getDF(SparkLib lib){


        Dataset<Row> df = lib.createDF("/Users/stanleyopara/software/data.csv")
                .withColumn("price",col("price").cast(DataTypes.DoubleType))
                .withColumn("volume",col("volume").cast(DataTypes.IntegerType))
                .withColumn("num_buys",col("num_buys").cast(DataTypes.IntegerType))
                .withColumn("num_sells",col("num_sells").cast(DataTypes.IntegerType));
        UDF1 udf =new UDF1<String, String>() {
            @Override
            public String call(String id) {
                return id.split("_")[0];
            }
        };
        lib.sqlContext.udf().register("CTOF", udf, DataTypes.StringType);
        return df.withColumn("symbol", callUDF("CTOF",col("_id")));
    }

    //TODO old way of data migration delete
    private void rub() throws NoSuchAlgorithmException, IOException {
        TimeMgr.setTime();
        String csvFile = "/Users/stanleyopara/software/data.csv";
        String symbols = "BTC-1ST,BTC-2GIVE,BTC-ABY,BTC-ADA,BTC-ADT,BTC-ADX,BTC-AEON,BTC-AGRS,BTC-AMP,BTC-ANT,BTC-ARDR,BTC-ARK,BTC-AUR,BTC-BAT,BTC-BAY,BTC-BCC,BTC-BCPT,BTC-BCY,BTC-BITB,BTC-BLITZ,BTC-BLK,BTC-BLOCK,BTC-BNT,BTC-BRK,BTC-BRX,BTC-BSD,BTC-BURST,BTC-BYC,BTC-CANN,BTC-CFI,BTC-CLAM,BTC-CLOAK,BTC-CLUB,BTC-COVAL,BTC-CPC,BTC-CRB,BTC-CRW,BTC-CURE ,BTC-CVC,BTC-DASH,BTC-DCR,BTC-DCT,BTC-DGB,BTC-DMD,BTC-DNT,BTC-DOGE,BTC-DOPE ,BTC-DTB,BTC-DYN,BTC-EBST,BTC-EDG,BTC-EFL,BTC-EGC,BTC-EMC,BTC-EMC2,BTC-ENG,BTC-ENRG ,BTC-ERC,BTC-ETC,BTC-ETH,BTC-EXCL,BTC-EXP,BTC-FAIR,BTC-FCT,BTC-FLDC,BTC-FTC,BTC-GAM,BTC-GAME,BTC-GBG,BTC-GBYTE,BTC-GCR,BTC-GEO,BTC-GLD,BTC-GNO,BTC-GNT,BTC-GOLOS,BTC-GRC,BTC-GRS,BTC-GUP,BTC-HMQ,BTC-IGNIS,BTC-INCNT,BTC-INFX,BTC-IOC,BTC-ION,BTC-IOP,BTC-KMD,BTC-KORE ,BTC-LBC,BTC-LGD,BTC-LMC,BTC-LRC,BTC-LSK,BTC-LTC,BTC-LUN,BTC-MAID,BTC-MANA,BTC-MCO,BTC-MEME ,BTC-MER,BTC-MLN,BTC-MONA,BTC-MUE,BTC-MUSIC,BTC-NAV,BTC-NBT,BTC-NEO,BTC-NEOS ,BTC-NLG,BTC-NMR,BTC-NXC,BTC-NXS,BTC-NXT,BTC-OK,BTC-OMG,BTC-OMNI,BTC-PART,BTC-PAY,BTC-PDC,BTC-PINK,BTC-PIVX,BTC-PKB,BTC-POT,BTC-POWR,BTC-PPC,BTC-PTC,BTC-PTOY,BTC-QRL,BTC-QTUM,BTC-QWARK,BTC-RADS,BTC-RCN,BTC-RDD,BTC-REP,BTC-RLC,BTC-SALT,BTC-SBD,BTC-SC,BTC-SEQ,BTC-SHIFT,BTC-SIB,BTC-SLR,BTC-SLS,BTC-SNRG,BTC-SNT,BTC-SPHR,BTC-SPR,BTC-SRN,BTC-START,BTC-STEEM,BTC-STORJ,BTC-STRAT,BTC-SWIFT,BTC-SWT,BTC-SYNX,BTC-SYS,BTC-THC,BTC-TIX,BTC-TKS,BTC-TRST,BTC-TRUST,BTC-TRX,BTC-TUSD,BTC-TX,BTC-UBQ,BTC-UKG,BTC-UNB,BTC-VEE,BTC-VIA,BTC-VIB,BTC-VOX,BTC-VRC,BTC-VRM,BTC-VTC,BTC-VTR,BTC-WAVES,BTC-WAX,BTC-WINGS,BTC-XCP,BTC-XDN,BTC-XEL,BTC-XEM,BTC-XLM,BTC-XMG,BTC-XMY,BTC-XRP,BTC-XST,BTC-XVC,BTC-XVG,BTC-XWC,BTC-XZC,BTC-ZCL,BTC-ZEC,BTC-ZEN,BTC-ZRX,USDT-ADA,USDT-BCC,USDT-BTC,USDT-BTG,USDT-DASH,USDT-ETC,USDT-ETH,USDT-LTC,USDT-NEO,USDT-NXT,USDT-OMG,USDT-XMR,USDT-XRP,USDT-XVG,USDT-ZEC";

        CSVReader reader = new CSVReader(new FileReader(csvFile));
        reader.readNext();
        Arrays.asList(symbols.split(",")).forEach(symbol -> {
            try {

                String[] line;

                List<Pojo> pojos = new ArrayList<>();
                while ((line = reader.readNext()) != null) {
                    if (line[0].contains("_") && line[0].startsWith(symbol)) {
                        Pojo pojo = new Pojo(line[0].split("_")[1],
                                line[1], line[2], line[3], line[4]);
                        pojos.add(pojo);
                    }
                }
                Writer writer = Files.newBufferedWriter(Paths.get("/Users/stanleyopara/software/data_symbols/" + symbol));
                StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                        .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                        .build();
                beanToCsv.write(pojos);
                System.out.println("Completed " + symbol);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static class Pojo{

        @CsvBindByPosition(position = 0)
        private String time;

        @CsvBindByPosition(position = 1)
        private double price;

        @CsvBindByPosition(position = 2)
        private int volume;

        @CsvBindByPosition(position = 3)
        private int num_buys;

        @CsvBindByPosition(position = 4)
        private int num_sells;

        public Pojo(String time, String price, String volume, String num_buys, String num_sells) {

            this.time = time;
            this.price = Double.parseDouble(price);
            this.volume = (int)Double.parseDouble(volume);
            this.num_buys = Integer.parseInt(num_buys);
            this.num_sells = Integer.parseInt(num_sells);
        }


        public String getTime() {
            return time;
        }

        public double getPrice() {
            return price;
        }

        public double getVolume() {
            return volume;
        }

        public int getNum_buys() {
            return num_buys;
        }

        public int getNum_sells() {
            return num_sells;
        }
    }*/
}
