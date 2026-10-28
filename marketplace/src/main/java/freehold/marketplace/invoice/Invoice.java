package freehold.marketplace.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class Invoice {
    private String invoiceId;
    private String orderId;
    private String goodsType;
    private int quantity;
    private int price;
    private int total;
    private Instant issuedAt;
}