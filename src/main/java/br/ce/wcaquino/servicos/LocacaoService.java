package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.exceptions.LocacaoException.*;
import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Date;
import java.util.List;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;

public class LocacaoService {
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoque, SemFilme, SemUsuario {

        if (filmes == null || filmes.size() == 0) throw new SemFilme();
        if (usuario == null) throw new SemUsuario();
	    if (filmes.stream().anyMatch((Filme filme) -> filme.getEstoque() == 0)) throw new FilmeSemEstoque();

		Locacao locacao = new Locacao();
		locacao.setFilme(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setValor(filmes.stream().mapToDouble(Filme::getPrecoLocacao).sum());

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		//TODO adicionar método para salvar
		
		return locacao;
	}


}