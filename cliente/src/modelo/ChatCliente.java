package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hércules M.
 */

public class ChatCliente {
    private Socket conexao;
    private Thread threadMensagens;
    private IChatCallback callback;
    
    public ChatCliente(IChatCallback callback) throws Exception{
        if(callback == null)
            throw new Exception("Objeto de callback é nulo.");
        
        this.callback = callback;
    }
    
    public static boolean portaValida(int porta){
        return (porta >= 0 && porta <= 65535);            
    }
    
    public static boolean ipValido(String ip){
        if(ip.isEmpty())
            return false;
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException ex) {
            return false;
        }
    }
    
    public void conectar(String ip, int porta) throws Exception, IOException
    {
        if(!ipValido(ip))
            throw new Exception("IP Inválido");
        
        if(!portaValida(porta))
            throw new Exception("Porta Inválida");
        
        conexao = new Socket(ip, porta);
        
        threadMensagens = new Thread(){
            @Override
            public void run(){
                processaMensagens();
            }
        };
        
        threadMensagens.start();
    }
    
    public void desconectar() throws IOException{
        if(conexao == null)
            return;
        
        if(conexao.isClosed())
            return;
              
        conexao.close();
        conexao = null;
        
        if(threadMensagens == null)
            return;
        
        threadMensagens.interrupt();
        threadMensagens = null;
        
    }
    
    public boolean enviarMensagem(String mensagem) {
        try {
            PrintWriter saida = new PrintWriter(conexao.getOutputStream(), true);
            saida.println(mensagem);
            return true;
        } catch (IOException ex) {
            callback.logCliente("[SISTEMA] Falha ao env. mensagem: " + ex.getMessage(), LogTipo.LOG_ERRO);
            return false;
        }
    }
    
    /**
     * Métodos Assíncronos (rodarão dentro de uma thread auxiliar)
     */
    
    private void processaMensagens(){
        while(!Thread.currentThread().isInterrupted()){
            try {
                BufferedReader buffer = new BufferedReader(new InputStreamReader(conexao.getInputStream(), "UTF8"));
                if(buffer.ready()){
                    String mensagem = buffer.readLine();
                    callback.mensagemRecebida(mensagem);
                }
            } catch (IOException ex) {
                callback.logCliente("[SISTEMA] Falha ao rec. mensagem: " + ex.getMessage(), LogTipo.LOG_ERRO);
            }
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                System.out.println("[ProcessaMensagens]Falha ao esperar: " + ex.getMessage());
            }
        }
    }
    
}
