package org.personal.crypto_watcher.controller.metrics.common;


import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class DescriptiveStatLib {


    public static double getMean(double[] input){

        DescriptiveStatistics descriptiveStats = new DescriptiveStatistics(input);
        return descriptiveStats.getMean();
    }

    public static double getSD(double[] input){

        DescriptiveStatistics descriptiveStats = new DescriptiveStatistics(input);
        return descriptiveStats.getStandardDeviation();
    }

    public static double computeReg(double[][] data){

        SimpleRegression regression = new SimpleRegression();
        regression.addData(data);
        return regression.getSlope();
    }

    public static double seqWidth(double[] input){

        DescriptiveStatistics descriptiveStats = new DescriptiveStatistics(input);
        double high = descriptiveStats.getPercentile(95);
        double low = descriptiveStats.getPercentile(5);
        double mean = descriptiveStats.getMean();
        if(input.length != 0 && mean != 0){
            return (high - low)/mean;
        }else{
            return 100;
        }
    }
}
