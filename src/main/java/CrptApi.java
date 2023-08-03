import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.Semaphore;

public class CrptApi {
    private int requestLimit;
    private long timeUnit;
    private Semaphore semaphore;

    String url = "https://ismp.crpt.ru/api/v3/lk/documents/create";
    String token = "token"; // поле для полученного токена

    public CrptApi(int requestLimit, long timeUnit) {
        this.requestLimit = requestLimit;
        this.timeUnit = timeUnit;
        this.semaphore = new Semaphore(requestLimit);
    }

    public void createDocument(HttpEntity request, String signature) throws InterruptedException {

        RestTemplate restTemplate = new RestTemplate();

        semaphore.acquire();
        try {

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class, signature);

            System.out.println(response.getStatusCode());
            Thread.sleep(timeUnit);
        } finally {
            semaphore.release();
        }
    }

    public HttpEntity newDocument(HeaderBodyDocument headerBodyDocument, Document document) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        Map<String, Object> jsonDate = new HashMap<>();
        jsonDate.putAll(headerBodyDocument.headerBodyDocument());
        jsonDate.putAll(document.document());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(jsonDate, headers);

        return request;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static
    class HeaderBodyDocument {

        private String document_format = "MANUAL";
        private String product_document = "<Документ в base64>";
        private String signature = "<Открепленная подпись в base64>";
        private String type = "LP_INTRODUCE_GOODS";

        public Map<String, String> headerBodyDocument() {

            Map<String, String> jsonHeaderBody = new HashMap<>();
            jsonHeaderBody.put("document_format", document_format);
            jsonHeaderBody.put("product_document", product_document);
            jsonHeaderBody.put("signature", signature);
            jsonHeaderBody.put("type", type);

            return jsonHeaderBody;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static
    class Document {
        private Description description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private ImportRequest importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private String production_date;
        private String production_type;
        private Products products;
        private String reg_date;
        private String reg_number;

        private Map<String, Object> document() {

            Map<String, Object> jsonDocument = new HashMap<>();
            jsonDocument.put("description", description);
            jsonDocument.put("doc_id", doc_id);
            jsonDocument.put("doc_status", doc_status);
            jsonDocument.put("doc_type", doc_type);
            jsonDocument.put("importRequest", importRequest);
            jsonDocument.put("owner_inn", owner_inn);
            jsonDocument.put("participant_inn", participant_inn);
            jsonDocument.put("producer_inn", producer_inn);
            jsonDocument.put("production_date", production_date);
            jsonDocument.put("production_type", production_type);
            jsonDocument.put("products", products.products());
            jsonDocument.put("reg_date", reg_date);
            jsonDocument.put("reg_number", reg_number);

            return jsonDocument;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Description {
        private String participantInn;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class ImportRequest {
        private boolean importRequest;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static
    class Products {
        private String certificate_document;
        private String certificate_document_date;
        private String certificate_document_number;
        private String owner_inn;
        private String producer_inn;
        private String production_date;
        private String tnved_code;
        private String uit_code;
        private String uitu_code;

        private Map<String, Object> products() {

            Map<String, Object> jsonProducts = new HashMap<>();
            jsonProducts.put("certificate_document", certificate_document);
            jsonProducts.put("certificate_document_date", certificate_document_date);
            jsonProducts.put("certificate_document_number", certificate_document_number);
            jsonProducts.put("owner_inn", owner_inn);
            jsonProducts.put("producer_inn", producer_inn);
            jsonProducts.put("production_date", production_date);
            jsonProducts.put("tnved_code", tnved_code);
            jsonProducts.put("uit_code", uit_code);
            jsonProducts.put("uitu_code", uitu_code);

            return jsonProducts;
        }
    }
}