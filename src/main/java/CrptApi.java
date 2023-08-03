import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.Semaphore;

public class CrptApi {
    private int requestLimit;
    private long timeUnit;
    private Semaphore semaphore;


    public CrptApi(int requestLimit, long timeUnit) {
        this.requestLimit = requestLimit;
        this.timeUnit = timeUnit;
        this.semaphore = new Semaphore(requestLimit);
    }

    private void createDocument() throws InterruptedException {

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://ismp.crpt.ru/api/v3/lk/documents/create";
        String token = "token"; // поле для полученного токена

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        Map<String, Object> jsonDate = new HashMap<>();
        jsonDate.put("document_format", "MANUAL");
        jsonDate.put("product_document", "<Документ в base64>");
        jsonDate.put("product_group", "Товарная группа");
        jsonDate.put("signature", "<Открепленная подпись в base64>");
        jsonDate.put("type", "LP_INTRODUCE_GOODS");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(jsonDate, headers);

        semaphore.acquire();
        try {
            restTemplate.postForEntity(url, request, String.class);
            Thread.sleep(timeUnit);
        } finally {
            semaphore.release();
        }
    }

    public static void main(String[] args) {

        CrptApi crptApi = new CrptApi(3, 5000);

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        crptApi.createDocument();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.start();
        }
    }
}