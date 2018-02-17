package org.personal.crypto_watcher.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jaxrs.json.annotation.JacksonFeatures;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import org.personal.crypto_watcher.model.BittrexResult;
import org.personal.crypto_watcher.model.CurrCap;
import org.personal.crypto_watcher.model.MarketCap;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

public class RestService {

    private static final String COIN_MKT_CAP = "https://api.coinmarketcap.com/v1/global/";
    private static final String CURR_MKT_CAP = "https://api.coinmarketcap.com/v1/ticker/?limit=300";
    private static final String BITTREX_MARKET = "https://bittrex.com/api/v1.1/public/getmarketsummaries";


    public static MarketCap getMarketCap(){

        WebResource webResource = getResource(COIN_MKT_CAP);
        return webResource.get(MarketCap.class);
    }

    public static List<CurrCap> getCurrCaps() throws IOException {

        String result = getResource(CURR_MKT_CAP).get(String.class);
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<CurrCap>> typeRef
                = new TypeReference<List<CurrCap>>() {};
        return mapper.readValue(result, typeRef);
    }

    public static BittrexResult getBittrexTradables(){

        WebResource webResource = getResource(BITTREX_MARKET);
        return webResource.get(BittrexResult.class);
    }


    public static String get(String url){

        WebResource webResource = getResource(url);
        return webResource.get(String.class);
    }

    public static WebResource getResource(String url){

        ObjectMapper mapper = new ObjectMapper();
        ClientConfig config = new DefaultClientConfig();
        config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(
                new HostnameVerifier() {
                    @Override
                    public boolean verify( String s, SSLSession sslSession ) {
                        return true;
                    }
                },getSSLContext()
        ));
        config.getSingletons().add(new JacksonJsonProvider(mapper));
        Client client = Client.create(config);

        return client.resource(url);
    }

    private static SSLContext getSSLContext () {

        try {

            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
                public X509Certificate[] getAcceptedIssuers(){return new X509Certificate[0];}
                public void checkClientTrusted(X509Certificate[] certs, String authType){}
                public void checkServerTrusted(X509Certificate[] certs, String authType){}
            }};
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            return sc;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
