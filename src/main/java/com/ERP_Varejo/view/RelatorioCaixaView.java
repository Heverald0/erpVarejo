package com.ERP_Varejo.view;

import com.ERP_Varejo.model.Venda;
import com.ERP_Varejo.service.VendaService;
import com.ERP_Varejo.service.ImpressoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class RelatorioCaixaView extends JFrame {

    @Autowired
    private VendaService vendaService;

    @Autowired
    private ImpressoraService impressoraService;

    private JTable tabelaVendas;
    private DefaultTableModel modelTabela;
    private JTextField txtDataInicio, txtDataFim;
    private JComboBox<String> comboTipoRelatorio;
    private JLabel lblTotalGeral, lblTotalPix, lblTotalDebito, lblTotalCredito, lblTotalDinheiro;

    public void exibir() {
        setTitle("Auditoria de Caixa - CasadosFogões");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- PAINEL DE FILTROS (NORTE) ---
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlFiltros.setBackground(new Color(236, 240, 241));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String hoje = LocalDate.now().format(dtf);

        txtDataInicio = new JTextField(hoje, 10);
        txtDataFim = new JTextField(hoje, 10);
        
        JButton btnConsultar = new JButton("CONSULTAR");
        
        // Seletor de Tipo de Relatório
        String[] tipos = {"Simplificado", "Detalhado"};
        comboTipoRelatorio = new JComboBox<>(tipos);
        
        // Botão Imprimir com texto simplificado
        JButton btnImprimir = new JButton("IMPRIMIR");
        btnImprimir.setBackground(new Color(46, 204, 113));
        btnImprimir.setForeground(Color.WHITE);

        pnlFiltros.add(new JLabel("Início:")); pnlFiltros.add(txtDataInicio);
        pnlFiltros.add(new JLabel("Fim:")); pnlFiltros.add(txtDataFim);
        pnlFiltros.add(btnConsultar);
        pnlFiltros.add(new JLabel("Tipo:")); pnlFiltros.add(comboTipoRelatorio);
        pnlFiltros.add(btnImprimir);

        // --- TABELA DE VENDAS (CENTRO) ---
        String[] colunas = {"ID", "Data/Hora", "Pagamento", "Operador", "Total"};
        modelTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaVendas = new JTable(modelTabela);
        add(new JScrollPane(tabelaVendas), BorderLayout.CENTER);

        // --- PAINEL DE TOTAIS (SUL) ---
        JPanel pnlTotais = new JPanel(new GridLayout(1, 5, 10, 10));
        pnlTotais.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlTotais.setBackground(new Color(44, 62, 80));

        lblTotalPix = criarLabel("PIX: R$ 0,00");
        lblTotalDebito = criarLabel("DÉBITO: R$ 0,00");
        lblTotalCredito = criarLabel("CRÉDITO: R$ 0,00");
        lblTotalDinheiro = criarLabel("DINHEIRO: R$ 0,00");
        lblTotalGeral = criarLabel("TOTAL: R$ 0,00");

        pnlTotais.add(lblTotalPix); pnlTotais.add(lblTotalDebito); 
        pnlTotais.add(lblTotalCredito); pnlTotais.add(lblTotalDinheiro); pnlTotais.add(lblTotalGeral);
        
        add(pnlFiltros, BorderLayout.NORTH);
        add(pnlTotais, BorderLayout.SOUTH);

        // --- EVENTOS ---
        btnConsultar.addActionListener(e -> carregarRelatorio());

        btnImprimir.addActionListener(e -> {
            try {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDateTime inicio = LocalDate.parse(txtDataInicio.getText(), format).atStartOfDay();
                LocalDateTime fim = LocalDate.parse(txtDataFim.getText(), format).atTime(LocalTime.MAX);
                List<Venda> vendas = vendaService.buscarVendasParaRelatorio(inicio, fim);
                
                if (vendas.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Não há vendas no período para imprimir.");
                    return;
                }

                // Verifica o tipo selecionado no JComboBox
                if (comboTipoRelatorio.getSelectedItem().equals("Detalhado")) {
                    impressoraService.imprimirRelatorioDetalhado(vendas, inicio, fim);
                } else {
                    // Note: Certifique-se de que o método simplificado existe na sua ImpressoraService
                    impressoraService.imprimirRelatorioSimplificado(vendas, inicio, fim);
                }
                
                JOptionPane.showMessageDialog(this, "Relatório enviado para a impressora!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro de Impressão: " + ex.getMessage());
            }
        });

        // Configuração do ESC para fechar
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc");
        this.getRootPane().getActionMap().put("esc", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { dispose(); }
        });

        setVisible(true);
    }

    private JLabel criarLabel(String t) {
        JLabel l = new JLabel(t); l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13)); return l;
    }

    private void carregarRelatorio() {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime inicio = LocalDate.parse(txtDataInicio.getText(), dtf).atStartOfDay();
            LocalDateTime fim = LocalDate.parse(txtDataFim.getText(), dtf).atTime(LocalTime.MAX);
            List<Venda> vds = vendaService.buscarVendasParaRelatorio(inicio, fim);
            
            modelTabela.setRowCount(0);
            BigDecimal tG = BigDecimal.ZERO, tP = BigDecimal.ZERO, tD = BigDecimal.ZERO, tC = BigDecimal.ZERO, tDi = BigDecimal.ZERO;

            for (Venda v : vds) {
                modelTabela.addRow(new Object[]{
                    v.getId(), 
                    v.getDataVenda().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")), 
                    v.getMetodoPagamento(), 
                    v.getUsuario().getNome(), 
                    String.format("R$ %.2f", v.getTotalVenda())
                });
                
                tG = tG.add(v.getTotalVenda());
                switch (v.getMetodoPagamento()) {
                    case PIX -> tP = tP.add(v.getTotalVenda());
                    case DEBITO -> tD = tD.add(v.getTotalVenda());
                    case CREDITO -> tC = tC.add(v.getTotalVenda());
                    case DINHEIRO -> tDi = tDi.add(v.getTotalVenda());
                }
            }
            
            lblTotalPix.setText(String.format("PIX: R$ %.2f", tP)); 
            lblTotalDebito.setText(String.format("DÉBITO: R$ %.2f", tD));
            lblTotalCredito.setText(String.format("CRÉDITO: R$ %.2f", tC)); 
            lblTotalDinheiro.setText(String.format("DINHEIRO: R$ %.2f", tDi));
            lblTotalGeral.setText(String.format("TOTAL: R$ %.2f", tG));
            
        } catch (Exception ex) { 
            JOptionPane.showMessageDialog(this, "Erro ao carregar: " + ex.getMessage()); 
        }
    }
}