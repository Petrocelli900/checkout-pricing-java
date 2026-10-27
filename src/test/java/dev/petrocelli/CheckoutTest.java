package dev.petrocelli;

import java.math.BigDecimal;
import java.util.*;

public final class CheckoutTest {
    private static BigDecimal d(String value){return new BigDecimal(value);}
    private static void check(boolean result){if(!result)throw new AssertionError();}
    private static void rejects(Class<? extends Throwable> type,Runnable action) {
        try{action.run();}catch(Throwable e){if(type.isInstance(e))return;throw e;}
        throw new AssertionError("Expected "+type);
    }
    public static void main(String[] args) {
        var policy=new Checkout.Policy(d("10"),d("15"),d("100"));
        var lines=new ArrayList<Checkout.Line>();
        lines.add(new Checkout.Line("book",2,d("60")));
        var quote=Checkout.quote(lines,policy);
        check(quote.subtotal().equals(d("120.00"))); check(quote.discount().equals(d("12.00")));
        check(quote.shipping().equals(d("0.00"))); check(quote.total().equals(d("108.00")));
        lines.clear(); check(quote.lines().size()==1);
        rejects(UnsupportedOperationException.class,()->quote.lines().clear());
        var below=Checkout.quote(List.of(new Checkout.Line("x",1,d("100"))),policy);
        check(below.shipping().equals(d("15.00"))); check(below.total().equals(d("105.00")));
        var rounding=Checkout.quote(List.of(new Checkout.Line("x",1,d("0.05"))),new Checkout.Policy(d("10"),d("0"),d("0")));
        check(rounding.discount().equals(d("0.01"))); check(rounding.total().equals(d("0.04")));
        var free=Checkout.quote(List.of(new Checkout.Line("x",1,d("50"))),new Checkout.Policy(d("100"),d("0"),d("0")));
        check(free.total().equals(d("0.00")));
        rejects(IllegalArgumentException.class,()->Checkout.quote(List.of(),policy));
        rejects(IllegalArgumentException.class,()->new Checkout.Policy(d("101"),d("0"),d("0")));
        rejects(ArithmeticException.class,()->new Checkout.Line("x",1,d("1.001")));
        rejects(IllegalArgumentException.class,()->new Checkout.Line("x",0,d("1")));
        rejects(IllegalArgumentException.class,()->Checkout.quote(List.of(new Checkout.Line("x",1,d("1")),new Checkout.Line("x",1,d("2"))),policy));
        System.out.println("Checkout: OK");
    }
}
