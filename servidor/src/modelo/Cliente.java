package modelo;

import java.net.Socket;

/**
 *
 * @author Hércules M.
 */
public class Cliente {
    private int id;
    private String apelido;
    private Socket conexao;
    private Thread threadMensagens;
    
    public Cliente(int id, String apelido, Socket conexao){
        this.id = id;
        this.apelido = apelido + id + "@" + conexao.getInetAddress().getHostAddress();
        this.conexao = conexao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido + "@" + conexao.getInetAddress().getHostAddress();
    }

    public Socket getConexao() {
        return conexao;
    }

    public void setConexao(Socket conexao) {
        this.conexao = conexao;
    }

    public Thread getThreadMensagens() {
        return threadMensagens;
    }

    public void setThreadMensagens(Thread processaMensagens) {
        this.threadMensagens = processaMensagens;
    }
    
    
    
    
}
