package modelo;

/**
 *
 * @author Hércules M.
 */
public interface IChatCallback {
    public void mensagemRecebida(String mensagem);
    public void logCliente(String mensagem, LogTipo tipo);
}
