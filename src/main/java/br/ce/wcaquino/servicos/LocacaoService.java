package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.exceptions.LocacaoException.*;
import static br.ce.wcaquino.utils.DataUtils.adicionarDias;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.LocacaoException;
import br.ce.wcaquino.exceptions.SPCException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {

    private LocacaoDAO dao;
    private SPCService spcService;
    private EmailService emailService;

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws Exception {

        if (filmes == null || filmes.size() == 0) throw new SemFilme();
        if (usuario == null) throw new SemUsuario();
	    if (filmes.stream().anyMatch((Filme filme) -> filme.getEstoque() == 0)) throw new FilmeSemEstoque();

	    if(spcService.possuiNegativacao(usuario)) {
	        throw new LocacaoException.UsuarioNegativoNoSPC();
        }

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
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
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
            dataEntrega = adicionarDias(dataEntrega, 1);
        }
		locacao.setDataRetorno(dataEntrega);

        //Salvando a locacao...
		dao.salvar(locacao);

		return locacao;
	}

	public void notificarAtrasos() {
	    dao.obterLocacoesPendentes()
                .stream()
                .filter(locacao -> locacao.getDataRetorno().before(new Date()))
                .map(Locacao::getUsuario)
                .forEach(emailService::notificarAtraso);
    }

    public void prorrogarAlocacao(Locacao locacao, int dias) {
	    Locacao novaLocacao = new Locacao();
	    novaLocacao.setUsuario(locacao.getUsuario());
	    novaLocacao.setValor(locacao.getValor() * dias);
	    novaLocacao.setFilmes(locacao.getFilme());
	    novaLocacao.setDataLocacao(new Date());
	    novaLocacao.setDataRetorno(obterDataComDiferencaDias(dias));
	    dao.salvar(novaLocacao);
    }
}