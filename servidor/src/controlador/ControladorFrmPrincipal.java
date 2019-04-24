package controlador;

import visao.FrmPrincipal;
import javax.swing.*;
import java.awt.event.ActionEvent;
import modelo.*;

/**
 *
 * @author Hércules M.
 */
public class ControladorFrmPrincipal implements IChatCallback{
    
    // Componentes
    private JTextField txtIp;
    private JTextField txtPorta;
    private JTextField txtConexoes;
    private JButton btnIniciar;
    private JTextPane txtLog;
    private FrmPrincipal form;
    
    // Minhas
    private final int MODO_INICIADO = 0;
    private final int MODO_PARADO = 1;
    private final int LOG_ERRO = 0;
    private final int LOG_SUCESSO = 1;
    private final int LOG_AVISO = 2;
    
    private int modoAtual = MODO_PARADO;
    public StringBuilder logBuffer = new StringBuilder();
    
    public ControladorFrmPrincipal(JTextField txtIp, JTextField txtPorta, JTextField txtConexoes, JButton btnIniciar, JTextPane txtLog, FrmPrincipal form){
        this.txtIp = txtIp;
        this.txtPorta = txtPorta;
        this.txtConexoes = txtConexoes;
        this.btnIniciar = btnIniciar;
        this.txtLog = txtLog;
        this.form = form;
    }
    
    private void modoInterface(int modo){
        modoAtual = modo;
        switch(modo) {
            case MODO_INICIADO:
                txtIp.setEnabled(false);
                txtPorta.setEnabled(false);
                txtConexoes.setEnabled(false);
                btnIniciar.setText("Parar");
                break;
            case MODO_PARADO:
                txtIp.setEnabled(true);
                txtPorta.setEnabled(true);
                txtConexoes.setEnabled(true);
                btnIniciar.setText("Iniciar");
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
    public synchronized void clienteConectado(Cliente cliente){
        
    }
    
    @Override
    public synchronized void clienteDesconectado(Cliente cliente){
        
    }
    
    @Override
    public synchronized void novaMensagem(Cliente cliente, String mensagem){
        
    }
    
    @Override
    public synchronized void logServidor(String mensagem){
        log(mensagem);
    }
    
    /**
     * Tratamento de eventos
     */
    public void btnIniciarActionPerformed(ActionEvent evt) {                                           
        // Rotina para teste...
        log("[Sistema] alternando modo...");
        if(modoAtual == MODO_PARADO){
            modoInterface(MODO_INICIADO);
            log("[Sistema] INICIADO", LOG_AVISO);
        }else{
            modoInterface(MODO_PARADO);
            log("[Sistema] PARADO", LOG_AVISO);
        }
    } 
    
}
