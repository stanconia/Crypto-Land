package org.personal.crypto_watcher.controller.metrics.trend_analyser;

public class TrendOutput {

    private double current;
    private double oneHr;
    private double twoHr;
    private double fourHr;
    private double sixHr;
    private double twelveHr;
    private double score;
    private double spread;


    private double additionalInfo;

    private String symbol;

    public TrendOutput(String symbol ,double current, double oneHr, double twoHr,
                       double fourHr,double sixHr,double twelveHr){

        this.current = current;
        this.oneHr = oneHr;
        this.twoHr = twoHr;
        this.fourHr = fourHr;
        this.sixHr = sixHr;
        this.twelveHr = twelveHr;
        this.symbol = symbol;
        this.additionalInfo = 0;
    }

    public double getOneHr() {
        return oneHr;
    }

    public double getTwoHr() {
        return twoHr;
    }

    public double getFourHr() {
        return fourHr;
    }

    public double getSixHr() {
        return sixHr;
    }

    public double getTwelveHr() {
        return twelveHr;
    }

    public double getScore(){ return score;}

    public double getCurrent() {
        return current;
    }

    public double getSpread() {
        return spread;
    }

    public double getAdditionalInfo() {
        return additionalInfo;
    }

    public void setSpread(double spread) {

        this.spread = spread;
    }

    public void setScore(double score){ this.score = score;}

    public void setAdditionalInfo(double additionalInfo){ this.additionalInfo = additionalInfo;}

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString(){

        StringBuilder builder = new StringBuilder();
        builder.append(symbol.toLowerCase() + "%7C")
                .append((int) score + "%7C")
                .append(  (int)current + "%7C")
                .append( (int) oneHr + "%7C")
                .append( (int) twoHr + "%7C")
                .append((int) fourHr + "%7C")
                .append( (int) sixHr + "%7C")
                .append( (int) twelveHr + "%7C%7C")
                .append( (int) additionalInfo);
        return builder.toString();
    }

}
