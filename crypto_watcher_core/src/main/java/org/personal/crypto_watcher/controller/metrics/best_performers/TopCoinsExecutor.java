package org.personal.crypto_watcher.controller.metrics.best_performers;


import java.util.*;

public class TopCoinsExecutor{

    private PriorityQueue<GeneralCurrencyStat> priceQueue;
    private PriorityQueue<GeneralCurrencyStat> volQueue;
    private PriorityQueue<GeneralCurrencyStat> bestCoinQueue;
    private static  final int QUEUE_CAPACITY = 5;

    public TopCoinsExecutor(){

        priceQueue = new PriorityQueue<GeneralCurrencyStat>(QUEUE_CAPACITY, (a, b) -> Double.compare(b.getPriceVel(),a.getPriceVel() ));
        volQueue = new PriorityQueue<GeneralCurrencyStat>(QUEUE_CAPACITY, (a, b) -> Double.compare(b.getVolVel(),a.getVolVel() ));
        bestCoinQueue = new PriorityQueue<GeneralCurrencyStat>(QUEUE_CAPACITY, (a, b) -> Double.compare(b.getValue(),a.getValue()));
    }

    public void execute(List<GeneralCurrencyStat> generalCurrencyStats) {

        generalCurrencyStats.stream().forEach(generalCurrencyStat -> addPojoToQueue(generalCurrencyStat));
    }

    private void addPojoToQueue(GeneralCurrencyStat generalCurrencyStat) {

        priceQueue.add(generalCurrencyStat);
        volQueue.add(generalCurrencyStat);
        bestCoinQueue.add(generalCurrencyStat);
    }

    public Map<String,List<GeneralCurrencyStat>> report() {

        Map<String,List<GeneralCurrencyStat>> result = new HashMap<>();
        result.put("top_price_gains", getTopStats(priceQueue));
        result.put("top_vol_gains", getTopStats(volQueue));
        result.put("top_avg_gains", getTopStats(bestCoinQueue));  //TODO
        return result;
    }

    private List<GeneralCurrencyStat> getTopStats(PriorityQueue<GeneralCurrencyStat> pq){

        int topNum = (pq.size() > 5) ? QUEUE_CAPACITY : pq.size();
        List<GeneralCurrencyStat> result = new ArrayList<>();
        for (int i =0; i < topNum; i++){
            result.add(pq.poll());
        }
        return result;
    }
}
