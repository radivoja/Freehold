package freehold.market.invoice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final S3Client s3Client;
    private final ObjectMapper objectMapper;

    @Value("${s3.bucket.invoices}")
    private String bucketName;

    public void generateAndUpload(String orderId, String goodsType, int quantity, int price) throws JsonProcessingException {
        String invoiceId = "INV-" + orderId.substring(0, 8).toUpperCase();

        Invoice invoice = new Invoice(
                invoiceId,
                orderId,
                goodsType,
                quantity,
                price,
                quantity * price,
                Instant.now()
        );
        String json = objectMapper.writeValueAsString(invoice);
        String key = "invoices/" + invoiceId + ".json";

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType("application/json")
                        .build(),
                RequestBody.fromString(json)
        );

    }
}