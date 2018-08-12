package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.SPCException;

public interface SPCService {

    boolean possuiNegativacao(Usuario usuario) throws SPCException.ErroInesperado;
}
