package modelo;

/**
 *
 * @author Hércules M.
 */
public interface IChatCallback {
    public void clienteConectado(Cliente cliente);
    public void clienteDesconectado(Cliente cliente);
    public void novaMensagem(Cliente cliente, String mensagem);
    public void logServidor(String mensagem, LogTipo tipo);
}
