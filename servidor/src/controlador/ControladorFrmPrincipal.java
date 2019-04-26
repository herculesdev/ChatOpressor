package controlador;

import visao.FrmPrincipal;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
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
    
    private int modoAtual = MODO_PARADO;
    public StringBuilder logBuffer = new StringBuilder();
    ChatServidor servidor;
    
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
                cor = "white";
                break;
            default:
                cor = "white";
        }
        
        String abreTag = "<span style=color:" + cor + ">";
        String fechaTag = "</span><br>";
        String texto = abreTag + mensagem + fechaTag;
        logBuffer.append(texto);
        txtLog.setText(logBuffer.toString());
        
    }
    
    private void log(String mensagem){
        log(mensagem, LogTipo.LOG_SUCESSO);
    }
    
    private boolean estaIniciado(){
        return (servidor != null);
    }
    
    private void iniciar(){        
        String ip = txtIp.getText();
        int porta = Integer.parseInt(txtPorta.getText());
        int max = Integer.parseInt(txtConexoes.getText());

        if(!ChatServidor.ipValido(ip)){
            return;
        }
        
        if(!ChatServidor.ipValido(ip)){
            return;
        }
        
        log("# Iniciando na porta " + porta + "...", LogTipo.LOG_AVISO);
        servidor = new ChatServidor(this);
        try {
            servidor.iniciar(ip, porta, max);
            log("Iniciado!", LogTipo.LOG_SUCESSO);
            modoInterface(MODO_INICIADO);
        } catch (Exception ex) {
            servidor = null;
            log("Falha: " + ex.getMessage(), LogTipo.LOG_ERRO);
        }
    }
    
    private void parar(){
        log("# Parando...", LogTipo.LOG_AVISO);
        servidor.parar();
        servidor = null;
        modoInterface(MODO_PARADO);
        log("Parado!", LogTipo.LOG_SUCESSO);
    }

    
    /**
     * Implementação da interface de callback
     */
    @Override
    public synchronized void clienteConectado(Cliente cliente){
        String ip = cliente.getConexao().getInetAddress().getHostAddress();
        log("Nova conexão: " + ip, LogTipo.LOG_AVISO);
    }
    
    @Override
    public synchronized void clienteDesconectado(Cliente cliente){
        String ip = cliente.getConexao().getInetAddress().getHostAddress();
        log(cliente.getApelido() + " desconectou-se", LogTipo.LOG_AVISO);        
    }
    
    @Override
    public synchronized void novaMensagem(Cliente cliente, String mensagem){
        log(cliente.getApelido() + " diz: " + mensagem, LogTipo.LOG_NORMAL);
    }
    
    @Override
    public synchronized void logServidor(String mensagem, LogTipo tipo){
        log(mensagem, tipo);
    }
    
    /**
     * Tratamento de eventos
     */
    public void btnIniciarActionPerformed(ActionEvent evt) {                                           
        if(!estaIniciado())
            iniciar();
        else
            parar();
    } 
    
    public void formWindowClosing(WindowEvent evt) {                                   
        if(estaIniciado())
            parar();
        
        Runtime.getRuntime().exit(0);
    }   
    
}
