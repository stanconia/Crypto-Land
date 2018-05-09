package org.personal.crypto_watcher.controller.alerts;

import com.textmagic.sdk.RestClient;
import com.textmagic.sdk.RestException;
import com.textmagic.sdk.resource.instance.TMNewMessage;

import java.util.Arrays;

public class TextAlert {

    private static final String MY_NUM = "+13125322119";
    private static final String USER_NAME = "stanleyopara";
    private static final String KEY = "wv4v2cTQS2y4wTUs1l8uiFKsp6KxTG";

    public static void send(String msg){

        RestClient client = new RestClient(USER_NAME, KEY);

        TMNewMessage m = client.getResource(TMNewMessage.class);
        m.setText(msg);
        m.setPhones(Arrays.asList(new String[] {MY_NUM}));
        try {
            m.send();
        } catch (final RestException e) {
            System.out.println(e.getErrors());
            //throw new RuntimeException(e);
        }
        System.out.println(m.getId());
    }
}
