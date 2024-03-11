package br.edu.infnet.order.utils;


import br.edu.infnet.order.domain.OrderStatus;
import br.edu.infnet.order.dto.response.OrderResponseDto;

import java.util.UUID;

public enum GeradorConteudoEmail {

    CRIACAO_ORDEM {
        @Override
        public String gerarTituloEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "Parabens %s sua ordem %s foi gerada com sucesso".formatted(
                    super.recuperarPrimeiroNome(nomeUsuario), orderResponse.getId());
        }

        @Override
        public String gerarConteudoEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "Sua ordem de %s %s no valor de %s foi gerada com sucesso. Conforme o status da sua compra for atualizado nós entraremos em contato com você. Muito obrigado pela sua compra."
                    .formatted(orderResponse.getTotalItens(),
                            orderResponse.getTotalItens() == 1 ? "itens" : "item",
                            orderResponse.getValorTotal());
        }

    },
    FALHA_PAGAMENTO_ORDER {
        @Override
        public String gerarTituloEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "Sentimos muito %s. Falha ao processar pagamento da sua ordem.".formatted(nomeUsuario);
        }

        @Override
        public String gerarConteudoEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "Infelizmente %s, não conseguimos processar o pagamento da sua ordem.".formatted(nomeUsuario) +
                    "Favor tente novamente em breve ou entre em contato com nosso atendimento.";
        }
    },
    REJEICAO_PAGAMENTO_ORDER {
        @Override
        public String gerarTituloEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "Ola %s. O pagamento da sua ordem foi rejeitado.".formatted(nomeUsuario);
        }

        @Override
        public String gerarConteudoEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "Infelizmente %s, o pagamento da ordem %s foi rejeitado.".formatted(nomeUsuario, orderResponse.getId()) +
                    "Entre em contato com a sua operadora de pagamento e tente novamente.";
        }
    },
    FALHA_CRIACAO_ORDEM {
        @Override
        public String gerarTituloEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "Sentimos muito %s. Falha criação da ordem.".formatted(super.recuperarPrimeiroNome(nomeUsuario));
        }

        @Override
        public String gerarConteudoEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "Sentimos muito %s. Não foi possível processar sua ordem neste momento.".formatted(this.recuperarPrimeiroNome(nomeUsuario)) +
                    "Se você realizou sua compra por cartão de crédito verifique se o valor foi descontado. " +
                    "Caso tenha feito por Pix ou boleto fique tranquilo o seu dinheiro será retornado em até 3 dias uteis. " +
                    "Caso tenha alguma dúvida entre em contato conosco.";
        }
    },
    ATUALIZACAO_ORDEM {
        @Override
        public String gerarTituloEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "Olhe %s. Sua ordem foi atualizada.".formatted(super.recuperarPrimeiroNome(nomeUsuario));
        }

        @Override
        public String gerarConteudoEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "Sua ordem foi atualizada para o status de %s. Entre em contato conosco em caso de dúvidas.".formatted(orderResponse.getOrderStatus().name());
        }
    },
    ORDEM_ENTREGUE {
        @Override
        public String gerarTituloEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "Parabens %s sua ordem foi entregue com sucesso.";
        }

        @Override
        public String gerarConteudoEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "Parabens %s sua ordem tão esperada finalmente chegou a sua residencia.".formatted(super.recuperarPrimeiroNome(nomeUsuario)) +
                    "Caso não tenha recebido o pedido verifique no site da transportadora quem recebeu." +
                    "Você tem até 7 dias para devolver os itens comprados em caso de arrependimento ou 30 dias em caso de defeito de fabrica." +
                    "Caso tenha alguma dúvida entre em contato conosco";
        }
    },
    SHOOPING_CART {
        @Override
        public String gerarTituloEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "%s, você ainda tem uma compra nao finalizada conosco.";
        }

        @Override
        public String gerarConteudoEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "Veja so %s, seu carrinho de compras ainda esta cheio!".formatted(nomeUsuario) +
                    "Finalize sua compra agora e aproveite nosso cupom de desconto." +
                    "Insira %s ao finalizar sua compra para ganhar R$ 15 reais de desconto".formatted(UUID.randomUUID().toString());
        }
    },
    FALHA_ATUALIZACAO_ORDEM {
        @Override
        public String gerarTituloEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "Opa %s. Falha atualização pedido.".formatted(super.recuperarPrimeiroNome(nomeUsuario));
        }

        @Override
        public String gerarConteudoEmail(OrderResponseDto orderResponse, String nomeUsuario) {
            return "Nos desculpe %s, mas houve uma falha ao atualizar o status da sua ordem.".formatted(super.recuperarPrimeiroNome(nomeUsuario)) +
                    "Caso essa seja a primeira vez que esteja recebendo esse email não se preocupe tentaremos atualizar sua ordem novamente em breve." +
                    "Caso tenha alguma dúvida entre em contato conosco";
        }
    };


    public abstract String gerarTituloEmail(OrderResponseDto orderResponse, String nomeUsuario);

    public abstract String gerarConteudoEmail(OrderResponseDto orderResponse, String nomeUsuario);

    protected String recuperarPrimeiroNome(String nomeCompleto) {
        final String[] nomes = nomeCompleto.split(" ");
        if(nomes.length > 0) {
            return nomes[0];
        } else {
            return nomeCompleto;
        }
    }

    public static GeradorConteudoEmail getGeradorFromOrderStatus(OrderStatus status) {
        switch (status) {
            case PROCESSING_PAYMENT, PAID:
                return GeradorConteudoEmail.CRIACAO_ORDEM;
            case REJECTED_PAYMENT:
                return GeradorConteudoEmail.REJEICAO_PAGAMENTO_ORDER;
            case FAILED_PAYMENT:
                return GeradorConteudoEmail.FALHA_PAGAMENTO_ORDER;
            case PROCESSING_DELIVERY, PROCESSING_RETURN, RETURNED, CANCELED:
                return GeradorConteudoEmail.ATUALIZACAO_ORDEM;
            case DELIVERED:
                return GeradorConteudoEmail.ORDEM_ENTREGUE;
            case SHOPPING_CART:
                return GeradorConteudoEmail.SHOOPING_CART;
            default:
                throw new RuntimeException("Tipo de order nao mapeado. %s".formatted(status.name()));
        }

    }

}
