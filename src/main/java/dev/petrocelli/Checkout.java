package dev.petrocelli;

import java.math.*;
import java.util.*;

/** Pure quote calculation. Values represent BRL with two decimal places. */
public final class Checkout {
    private static final BigDecimal ZERO=new BigDecimal("0.00");
    private Checkout() {}
    private static BigDecimal money(BigDecimal value) {
        Objects.requireNonNull(value);
        var normalized=value.setScale(2,RoundingMode.UNNECESSARY);
        if(normalized.signum()<0)throw new IllegalArgumentException("Negative money");
        return normalized;
    }
    public record Line(String sku,int quantity,BigDecimal unitPrice) {
        public Line {
            if(sku==null || sku.isBlank() || quantity<1)throw new IllegalArgumentException("Invalid line");
            unitPrice=money(unitPrice);
        }
        public BigDecimal subtotal(){return unitPrice.multiply(BigDecimal.valueOf(quantity));}
    }
    public record Policy(BigDecimal discountPercent,BigDecimal shipping,BigDecimal freeShippingThreshold) {
        public Policy {
            Objects.requireNonNull(discountPercent);
            if(discountPercent.signum()<0 || discountPercent.compareTo(new BigDecimal("100"))>0)
                throw new IllegalArgumentException("Discount outside 0..100");
            shipping=money(shipping); freeShippingThreshold=money(freeShippingThreshold);
        }
    }
    public record Quote(List<Line> lines,BigDecimal subtotal,BigDecimal discount,
                        BigDecimal shipping,BigDecimal total) {
        public Quote {lines=List.copyOf(lines);}
    }
    public static Quote quote(List<Line> input,Policy policy) {
        Objects.requireNonNull(input); Objects.requireNonNull(policy);
        var lines=List.copyOf(input);
        if(lines.isEmpty())throw new IllegalArgumentException("Empty cart");
        var skus=new HashSet<String>();
        for(var line:lines)if(!skus.add(line.sku()))throw new IllegalArgumentException("Duplicate SKU");
        var subtotal=lines.stream().map(Line::subtotal).reduce(ZERO,BigDecimal::add);
        var discount=subtotal.multiply(policy.discountPercent()).movePointLeft(2).setScale(2,RoundingMode.HALF_UP);
        var net=subtotal.subtract(discount);
        var shipping=net.compareTo(policy.freeShippingThreshold())>=0?ZERO:policy.shipping();
        return new Quote(lines,subtotal,discount,shipping,net.add(shipping));
    }
}
