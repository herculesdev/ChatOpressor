package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 *
 * @author Hércules M.
 */
public class ChatServidor {
    private ServerSocket servidor;
    private ArrayList<Cliente> clientes;
    private Thread aceitaConexoes;
    private IChatCallback callback;
    private int maxConexoes = 100;
    private int contadorId = 0;
    
    public ChatServidor(IChatCallback callback){
        this.callback = callback;
        clientes = new ArrayList();
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
    
    private int numConexoes(){
        return clientes.size();
    }
    
    private void desconectarCliente(Cliente cliente){
        if(cliente == null)
            return;
        
        try {
            cliente.getConexao().close();
            cliente.getProcessaMensagens().interrupt();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void desconectarTodosClientes(){
        if(clientes == null)
            return;
        if(numConexoes() == 0)
            return;
        
        for(int i = 0; i < numConexoes(); i++)
        {            
            desconectarCliente(clientes.get(i));
            clientes.remove(i);
        }
    }
    
    public void iniciar(String ip, int porta, int maxConexoes) throws IOException, Exception{
        if(!ipValido(ip))
            throw new Exception("IP Inválido");
        if(!portaValida(porta))
            throw new Exception("Porta Inválida");
        
        if(ip.equals("localhost")){
            servidor = new ServerSocket(porta);
        }else{
            servidor = new ServerSocket();
            InetSocketAddress endereco = new InetSocketAddress(ip, porta);
            servidor.bind(endereco);
        }
        
        aceitaConexoes = new Thread(){
            @Override
            public void run(){
                aceitarConexoes();
            }
        };
        aceitaConexoes.start();
    }
    
    public void parar(){
        aceitaConexoes.interrupt();
        desconectarTodosClientes();
        try {
            servidor.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    /**
     * Métodos assícronos (será executado em thread paralela)
     */
    private void aceitarConexoes(){
        while(!Thread.currentThread().isInterrupted()){
            
            // Se já atingiu limite de conexões
            if(numConexoes() >= maxConexoes){
                synchronized(this){
                    try {
                        Thread.currentThread().wait(1500);
                        continue;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            
            // Tenta aceitar conexão
            try {
                Socket conexao = servidor.accept();
                Cliente cliente = new Cliente(contadorId, "usuario", conexao);
                
                // Cria thread para processar as mensagens do cliente recem conectado
                Thread processaMensagens = new Thread(){
                    @Override
                    public void run(){
                        processaMensagens(cliente);
                    }
                };
                processaMensagens.start();
                
                cliente.setProcessaMensagens(processaMensagens);
                clientes.add(cliente);
                callback.clienteConectado(cliente);
                contadorId++;
            } catch (IOException ex) {
                callback.logServidor("Falha ao acc. conexao: " + ex.getMessage(), LogTipo.LOG_ERRO);
            }
        }
    }
    
    private void processaMensagens(Cliente cliente)
    {
        
        if(cliente == null)
            return;
        
        while(!Thread.currentThread().isInterrupted()){
            
            try {
                BufferedReader buffer = new BufferedReader(new InputStreamReader(cliente.getConexao().getInputStream(), "UTF8"));
                if(buffer.ready()){
                    String mensagem = buffer.readLine();                    
                    enviarMensagemTodos(cliente, mensagem);
                    callback.novaMensagem(cliente, mensagem);
                }
            } catch (IOException ex) {
                callback.logServidor("Falha ao ler mensagem: " + ex.getMessage(), LogTipo.LOG_ERRO);
            }
            
                synchronized(this){
                    try {
                        Thread.currentThread().wait(900);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
        }
    }
    
    private void enviarMensagem(Cliente remetente, Cliente destinatario, String mensagem){

        if(remetente == null)
            return;
        if(destinatario == null)
            return; 
        
        mensagem = remetente.getApelido() + " diz: " + mensagem;        
        try {
            PrintWriter saida = new PrintWriter(destinatario.getConexao().getOutputStream(), true);
            saida.println(mensagem); 
            System.out.println("Enviado para: " + destinatario.getApelido());
        } catch (IOException ex) {
            callback.logServidor("Falha ao env. mensagem: " + ex.getMessage(), LogTipo.LOG_ERRO);
        }
    }
    
    private void enviarMensagemTodos(Cliente remetente, String mensagem){
       for(int i = 0; i < numConexoes(); i++) {
           if(clientes.get(i).equals(remetente))
               continue;
           
        enviarMensagem(remetente, clientes.get(i), mensagem);   
           
       }
    }
    
}
