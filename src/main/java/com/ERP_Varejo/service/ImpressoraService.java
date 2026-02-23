package com.ERP_Varejo.service;

import com.ERP_Varejo.model.Venda;
import org.springframework.stereotype.Service;
import javax.print.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ImpressoraService {

    public void imprimirViaUSB(List<Venda> vendas, LocalDateTime inicio, LocalDateTime fim) throws Exception {
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        
        if (service == null) {
            throw new Exception("Impressora não detectada.");
        }

        // Ajustamos para 42 colunas para garantir que nada saia cortado nas bordas
        int largura = 42; 
        String conteudo = gerarCupomFechamento(vendas, inicio, fim, largura);
        
        // Comandos ESC/POS nativos: Reset + Texto + Corte
        char esc = (char) 27;
        String reset = esc + "@"; 
        String comandoCorte = "\n\n\n\n" + esc + "m"; 
        String documentoFinal = reset + conteudo + comandoCorte;

        // O SEGREDO PARA TIRAR O DELAY E O CORTE:
        // Enviamos BYTES DIRETOS. Isso ignora as margens de página do Windows.
        byte[] bytes = documentoFinal.getBytes("CP850"); 
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(bytes, flavor, null);
        
        DocPrintJob job = service.createPrintJob();
        job.print(doc, null);
        
        System.out.println("Impressão enviada em modo RAW (Sem delay e sem cortes)");
    }

    private String gerarCupomFechamento(List<Venda> vendas, LocalDateTime inicio, LocalDateTime fim, int largura) {
        StringBuilder cupom = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        cupom.append(centralizar("CASA DOS FOGÕES", largura)).append("\n");
        cupom.append(centralizar("AUDITORIA DE CAIXA", largura)).append("\n");
        cupom.append("-".repeat(largura)).append("\n");
        cupom.append("Inicio: ").append(inicio.format(dtf)).append("\n");
        cupom.append("Fim   : ").append(fim.format(dtf)).append("\n");
        cupom.append("-".repeat(largura)).append("\n\n");

        BigDecimal tPix = BigDecimal.ZERO, tDeb = BigDecimal.ZERO, tCre = BigDecimal.ZERO, tDin = BigDecimal.ZERO;

        for (Venda v : vendas) {
            switch (v.getMetodoPagamento()) {
                case PIX -> tPix = tPix.add(v.getTotalVenda());
                case DEBITO -> tDeb = tDeb.add(v.getTotalVenda());
                case CREDITO -> tCre = tCre.add(v.getTotalVenda());
                case DINHEIRO -> tDin = tDin.add(v.getTotalVenda());
            }
        }

        cupom.append(formatarLinha("PIX:", tPix, largura));
        cupom.append(formatarLinha("DEBITO:", tDeb, largura));
        cupom.append(formatarLinha("CREDITO:", tCre, largura));
        cupom.append(formatarLinha("DINHEIRO:", tDin, largura));
        cupom.append("-".repeat(largura)).append("\n");
        
        BigDecimal total = tPix.add(tDeb).add(tCre).add(tDin);
        cupom.append(formatarLinha("TOTAL GERAL:", total, largura));
        
        cupom.append("\n\n").append(centralizar("Fim do Relatorio", largura)).append("\n");

        return cupom.toString();
    }

    private String centralizar(String t, int l) {
        int e = (l - t.length()) / 2;
        return " ".repeat(Math.max(0, e)) + t;
    }

    private String formatarLinha(String label, BigDecimal v, int l) {
        String vStr = String.format("R$ %.2f", v);
        int esp = l - label.length() - vStr.length();
        return label + " ".repeat(Math.max(0, esp)) + vStr + "\n";
    }
}