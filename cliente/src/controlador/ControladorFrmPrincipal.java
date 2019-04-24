package controlador;

import visao.FrmPrincipal;
import modelo.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    private StringBuilder logBuffer = new StringBuilder();
    private int modoAtual = MODO_DESCONECTADO;
    ChatCliente chatCliente;
    
    
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
    
    private void log(String mensagem, LogTipo tipo){
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
            case LOG_NORMAL:
                cor = "black";
                break;
            default:
                cor = "black";
        }

        String abreTag = "<span style=color:" + cor + ">";
        String fechaTag = "</span><br>";
        String texto = abreTag + mensagem + fechaTag;
        logBuffer.append(texto);
        txtLog.setText(logBuffer.toString());
    }

    private void log(String mensagem){
        log(mensagem, LogTipo.LOG_NORMAL);
    }
    
    private boolean estaConectado(){
        return (chatCliente != null);
    }
    
    private boolean mensagemValida(String mensagem){
        return (!mensagem.isEmpty());
    }
    
    private void conecta(){
        String ip = txtIp.getText();
        int porta = Integer.parseInt(txtPorta.getText());
        
        if(!ChatCliente.portaValida(porta)){
            JOptionPane.showMessageDialog(form, "Porta inválida", "Erro: porta", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if(!ChatCliente.ipValido(ip)){
            JOptionPane.showMessageDialog(form, "Endereço IP inválido", "Erro: IP", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        log("# Conectando...", LogTipo.LOG_AVISO);
        try {
            chatCliente = new ChatCliente(this);
            chatCliente.conectar(ip, porta);
            log("Conectado!", LogTipo.LOG_SUCESSO);
            modoInterface(MODO_CONECTADO);
        } catch (Exception ex) {
            chatCliente = null;
            log("Falha: " + ex.getMessage(), LogTipo.LOG_ERRO);
        }
    }
    
    private void desconecta(){
        log("# Desconectando...", LogTipo.LOG_AVISO);
        try {
            chatCliente.desconectar();
            chatCliente = null;
            log("Desconectado!", LogTipo.LOG_SUCESSO);
            modoInterface(MODO_DESCONECTADO);
        } catch (IOException ex) {
            log("Falha: " + ex.getMessage(), LogTipo.LOG_ERRO);
        }        
    }
    
    /**
     * Implementação da interface de callback
     */
    @Override
    public synchronized void mensagemRecebida(String mensagem){
        log(mensagem);
    }
    
    public synchronized void logCliente(String mensagem, LogTipo tipo){
        log(mensagem, tipo);
    }
    
    /**
     * Tratamento de eventos
     */
    public void btnConectarActionPerformed(ActionEvent evt) {
        if(estaConectado())
            desconecta();
        else
            conecta();
    }
    
    public void btnEnviarActionPerformed(ActionEvent evt) {    
        String mensagem = txtMensagem.getText();
        if(!mensagemValida(mensagem) || !estaConectado())
            return;
      
        if(chatCliente.enviarMensagem(mensagem))
            log("Você diz: " + mensagem);
        txtMensagem.setText("");
    } 
    
    public void txtMensagemKeyReleased(java.awt.event.KeyEvent evt) {                                        
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
            btnEnviarActionPerformed(null);
    }
}
