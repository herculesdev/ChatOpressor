package controlador;

import visao.FrmPrincipal;
import modelo.*;
import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *
 * @author Hércules M.
 */
public class ControladorFrmPrincipal implements IChatCallback {
    
    // Componentes
    private JTextField txtIp;
    private JTextField txtPorta;
    private JButton btnConectar;
    private JTextPane txtLog;
    private JTextField txtMensagem;
    private JButton btnEnviar;
    private FrmPrincipal form;
    
    // Minhas
    private final int MODO_CONECTADO = 0;
    private final int MODO_DESCONECTADO = 1;
    
    private final int LOG_ERRO = 0;
    private final int LOG_SUCESSO = 1;
    private final int LOG_AVISO = 2;
    
    private StringBuilder logBuffer = new StringBuilder();
    private int modoAtual = MODO_DESCONECTADO;
    
    
    public ControladorFrmPrincipal(JTextField txtIp, JTextField txtPorta, JButton btnConectar, JTextPane txtLog, JTextField txtMensagem, JButton btnEnviar, FrmPrincipal form){
        this.txtIp = txtIp;
        this.txtPorta = txtPorta;
        this.btnConectar = btnConectar;
        this.txtLog = txtLog;
        this.txtMensagem = txtMensagem;
        this.btnEnviar = btnEnviar;
        this.form = form;
    }
    
    private void modoInterface(int modo){
        modoAtual = modo;
        switch(modo) {
            case MODO_CONECTADO:
                txtIp.setEnabled(false);
                txtPorta.setEnabled(false);
                txtMensagem.setEnabled(true);
                btnEnviar.setEnabled(true);
                btnConectar.setText("Desconectar");
                break;
            case MODO_DESCONECTADO:
                txtIp.setEnabled(true);
                txtPorta.setEnabled(true);
                txtMensagem.setEnabled(false);
                btnEnviar.setEnabled(false);
                btnConectar.setText("Conectar");
                break;
        }
    }
    
    private void log(String mensagem, int tipo){
        String cor = "blue";
        switch(tipo){
            case LOG_ERRO:
                cor = "red";
                break;
            case LOG_SUCESSO:
                cor = "green";
                break;
            case LOG_AVISO:
                cor= "orange";
                break;
            default:
                cor = "green";
        }

        String abreTag = "<span style=color:" + cor + ">";
        String fechaTag = "</span><br>";
        String texto = abreTag + mensagem + fechaTag;
        logBuffer.append(texto);
        txtLog.setText(logBuffer.toString());

    }

    private void log(String mensagem){
        log(mensagem, LOG_SUCESSO);
    }
    
    /**
     * Implementação da interface de callback
     */
    @Override
    public synchronized void mensagemRecebida(String mensagem){
        log(mensagem);
    }
    
    /**
     * Tratamento de eventos
     */
    public void btnConectarActionPerformed(ActionEvent evt) {    
        // Rotina para teste...
        log("[Sistema] alternando modo...");
        if(modoAtual == MODO_DESCONECTADO){
            modoInterface(MODO_CONECTADO);
            log("[Sistema] CONECTADO", LOG_AVISO);
        }else{
            modoInterface(MODO_DESCONECTADO);
            log("[Sistema] DESCONECTADO", LOG_AVISO);
        }
    }  
}
