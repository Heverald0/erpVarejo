package com.ERP_Varejo.service;

import com.ERP_Varejo.model.Venda;
import com.ERP_Varejo.model.ItemVenda;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

@Service
public class ImpressoraService {

    public String gerarLayoutCupom(Venda venda) {
        StringBuilder cupom = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        cupom.append("      CASADOSFOGOES - ERP      \n");
        cupom.append("--------------------------------\n");
        cupom.append("Data: ").append(venda.getDataVenda().format(dtf)).append("\n");
        cupom.append("Cupom: ").append(venda.getId()).append("\n");
        cupom.append("--------------------------------\n");
        cupom.append("ITEM   DESC   QTD   UN   TOTAL \n");

        for (ItemVenda item : venda.getItens()) {
            cupom.append(String.format("%-6s %-6s %-3d  %-5s %-6s\n", 
                item.getProduto().getId(),
                item.getProduto().getNome().substring(0, Math.min(item.getProduto().getNome().length(), 6)),
                item.getQuantidade(),
                item.getPrecoUnitarioHistorico(),
                item.getPrecoUnitarioHistorico().multiply(new java.math.BigDecimal(item.getQuantidade()))
            ));
        }

        cupom.append("--------------------------------\n");
        cupom.append("TOTAL: R$ ").append(venda.getTotalVenda()).append("\n");
        cupom.append("PAGAMENTO: ").append(venda.getMetodoPagamento()).append("\n");
        cupom.append("--------------------------------\n");
        cupom.append("     OBRIGADO PELA PREFERENCIA!   \n\n\n");

        return cupom.toString();
    }
}