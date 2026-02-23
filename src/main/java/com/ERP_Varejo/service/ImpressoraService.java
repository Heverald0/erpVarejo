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

    private final int LARGURA_BOBINA = 42; // Definido para evitar cortes
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // --- RELATÓRIO SIMPLIFICADO ---
    public void imprimirRelatorioSimplificado(List<Venda> vendas, LocalDateTime inicio, LocalDateTime fim) {
        new Thread(() -> {
            StringBuilder sb = iniciarCabecalho("RELATORIO SIMPLIFICADO", inicio, fim);
            
            BigDecimal[] totais = calcularTotais(vendas);
            adicionarFechamento(sb, totais[0], totais[1], totais[2], totais[3]);
            
            sb.append("\n").append(centralizar("Fim do Relatorio Simplificado", LARGURA_BOBINA)).append("\n");
            enviarParaImpressora(sb.toString());
        }).start();
    }

    // --- RELATÓRIO DETALHADO ---
    public void imprimirRelatorioDetalhado(List<Venda> vendas, LocalDateTime inicio, LocalDateTime fim) {
        new Thread(() -> {
            StringBuilder sb = iniciarCabecalho("RELATORIO DETALHADO", inicio, fim);

            BigDecimal tPix = BigDecimal.ZERO, tDeb = BigDecimal.ZERO, tCre = BigDecimal.ZERO, tDin = BigDecimal.ZERO;

            for (Venda v : vendas) {
                String hora = v.getDataVenda().format(DateTimeFormatter.ofPattern("HH:mm"));
                sb.append(String.format("VENDA: %-6d  HORA: %s\n", v.getId(), hora));
                sb.append("FORMA PGTO: ").append(v.getMetodoPagamento()).append("\n");
                sb.append(formatarLinha("SUBTOTAL:", v.getTotalVenda(), LARGURA_BOBINA));
                sb.append(".".repeat(LARGURA_BOBINA)).append("\n");

                switch (v.getMetodoPagamento()) {
                    case PIX -> tPix = tPix.add(v.getTotalVenda());
                    case DEBITO -> tDeb = tDeb.add(v.getTotalVenda());
                    case CREDITO -> tCre = tCre.add(v.getTotalVenda());
                    case DINHEIRO -> tDin = tDin.add(v.getTotalVenda());
                }
            }

            adicionarFechamento(sb, tPix, tDeb, tCre, tDin);
            sb.append("\n").append(centralizar("Fim do Relatorio Detalhado", LARGURA_BOBINA)).append("\n");
            enviarParaImpressora(sb.toString());
        }).start();
    }

    // --- MÉTODOS AUXILIARES DE FORMATAÇÃO E HARDWARE ---

    private StringBuilder iniciarCabecalho(String titulo, LocalDateTime inicio, LocalDateTime fim) {
        StringBuilder sb = new StringBuilder();
        sb.append(centralizar("CASA DOS FOGÕES", LARGURA_BOBINA)).append("\n");
        sb.append(centralizar(titulo, LARGURA_BOBINA)).append("\n");
        sb.append("-".repeat(LARGURA_BOBINA)).append("\n");
        sb.append("Periodo: ").append(inicio.format(dtf)).append("\n");
        sb.append("Ate    : ").append(fim.format(dtf)).append("\n");
        sb.append("-".repeat(LARGURA_BOBINA)).append("\n\n");
        return sb;
    }

    private void adicionarFechamento(StringBuilder sb, BigDecimal pix, BigDecimal deb, BigDecimal cre, BigDecimal din) {
        sb.append("\n").append("=".repeat(LARGURA_BOBINA)).append("\n");
        sb.append(centralizar("FECHAMENTO POR OPERACAO", LARGURA_BOBINA)).append("\n");
        sb.append("=".repeat(LARGURA_BOBINA)).append("\n");
        sb.append(formatarLinha("TOTAL DINHEIRO:", din, LARGURA_BOBINA));
        sb.append(formatarLinha("TOTAL PIX:", pix, LARGURA_BOBINA));
        sb.append(formatarLinha("TOTAL DEBITO:", deb, LARGURA_BOBINA));
        sb.append(formatarLinha("TOTAL CREDITO:", cre, LARGURA_BOBINA));
        sb.append("-".repeat(LARGURA_BOBINA)).append("\n");
        BigDecimal total = pix.add(deb).add(cre).add(din);
        sb.append(formatarLinha("TOTAL GERAL:", total, LARGURA_BOBINA));
    }

    private BigDecimal[] calcularTotais(List<Venda> vendas) {
        BigDecimal p = BigDecimal.ZERO, d = BigDecimal.ZERO, c = BigDecimal.ZERO, din = BigDecimal.ZERO;
        for (Venda v : vendas) {
            switch (v.getMetodoPagamento()) {
                case PIX -> p = p.add(v.getTotalVenda());
                case DEBITO -> d = d.add(v.getTotalVenda());
                case CREDITO -> c = c.add(v.getTotalVenda());
                case DINHEIRO -> din = din.add(v.getTotalVenda());
            }
        }
        return new BigDecimal[]{p, d, c, din};
    }

    private void enviarParaImpressora(String conteudo) {
        try {
            PrintService service = PrintServiceLookup.lookupDefaultPrintService();
            if (service == null) return;

            char esc = (char) 27;
            String documento = (esc + "@") + conteudo + ("\n\n\n\n\n" + esc + "m");
            byte[] bytes = documento.getBytes("CP850");
            
            DocPrintJob job = service.createPrintJob();
            job.print(new SimpleDoc(bytes, DocFlavor.BYTE_ARRAY.AUTOSENSE, null), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String centralizar(String texto, int largura) {
        int espacos = (largura - texto.length()) / 2;
        return " ".repeat(Math.max(0, espacos)) + texto;
    }

    private String formatarLinha(String label, BigDecimal valor, int largura) {
        String vStr = String.format("R$ %.2f", valor);
        int esp = largura - label.length() - vStr.length();
        return label + " ".repeat(Math.max(0, esp)) + vStr + "\n";
    }
}