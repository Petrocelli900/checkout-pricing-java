# Decisões técnicas

## Regras explícitas

Valores são BRL com duas casas decimais. Itens têm quantidade inteira positiva e preço não negativo; preço com casas excedentes que exigem arredondamento é rejeitado. Carrinho vazio e SKU duplicado são inválidos; o chamador deve agregar quantidades por SKU.

Subtotal soma preço vezes quantidade. O percentual de desconto (0..100) é aplicado ao subtotal e arredondado uma única vez com HALF_UP. A faixa de frete grátis é avaliada sobre o valor após desconto. Total = subtotal - desconto + frete. Uma faixa zero torna o frete grátis para qualquer carrinho.

## Função pura

O cálculo não lê banco, rede ou relógio. Os records normalizam valores de entrada e o resultado copia a lista de itens, evitando mudanças posteriores no carrinho. Calcular uma proposta não reserva estoque nem cobra pagamento.

## Limitações

Sem impostos, parcelamento, múltiplas moedas, cupons acumuláveis ou promoções por produto. Nenhuma integração financeira. Regras comerciais são demonstrativas, não uma reprodução de um checkout de produção.

