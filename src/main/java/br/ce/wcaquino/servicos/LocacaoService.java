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

		double valorDaLocacao = 0D;

		for (int i = 0; i < filmes.size(); i++) {
		    double valorFilme = filmes.get(i).getPrecoLocacao();
		    switch (i) {
                case 2: valorFilme = valorFilme * .75; break; // terceiro filme: desconto de 25%
                case 3: valorFilme = valorFilme * .50; break; // quarto filme: desconto de 50%
                case 4: valorFilme = valorFilme * .25; break; // quinto filme: desconto de 50%
                case 5: valorFilme = valorFilme * 0.0; break; // sexto filme: desconto de 100%
            }
            valorDaLocacao += valorFilme;
        }

		locacao.setValor(valorDaLocacao);


		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		//TODO adicionar método para salvar
		
		return locacao;
	}


}