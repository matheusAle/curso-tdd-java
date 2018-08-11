package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.LocacaoException;
import br.ce.wcaquino.matchers.AppMatchers;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.matchers.AppMatchers.caiEm;
import static br.ce.wcaquino.matchers.AppMatchers.ehHojeComDiferencaDeDias;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocacaoServiceTest {

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private LocacaoService locacaoService;
    private Usuario usuario;
    private List<Filme> filmes;

    @Before
    public void setup() {
        locacaoService = new LocacaoService();
        usuario = new Usuario("Usuario 1");
        filmes = new ArrayList<Filme>();
        filmes.add(new Filme("Filme 1", 1, 4.0));
        filmes.add(new Filme("Filme 2", 2, 4.0));
    }

    @Test()
    public void deveAlugarFilme() throws Exception {
        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
        error.checkThat(locacao.getValor(), is(equalTo(4.0 * filmes.size())));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDeDias(1));
    }

    @Test
    public void deveLancarExcecaoAoTentarAlugarFilmeSemEstoque() throws Exception {
        exception.expect(LocacaoException.FilmeSemEstoque.class);
        filmes.get(0).setEstoque(0);
        locacaoService.alugarFilme(usuario, filmes);
    }

    @Test
    public void deveLancarExcecaoAoTentarAlugarFilmeSemUsuario() throws Exception {
        exception.expect(LocacaoException.SemUsuario.class);
        locacaoService.alugarFilme(null, filmes);
    }

    @Test
    public void deveLancarExcecaoAoTentarAlugarFilmeComFilmesParamEmNull() throws Exception {
        exception.expect(LocacaoException.SemFilme.class);
        locacaoService.alugarFilme(usuario, null);
    }

    @Test
    public void deveLancarExcecaoAoTentarAlugarFilmeComFilmesParamComListaVazia() throws Exception {
        exception.expect(LocacaoException.SemFilme.class);
        locacaoService.alugarFilme(usuario, new ArrayList<Filme>());
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarFilmeNoSabado() throws LocacaoException.FilmeSemEstoque, LocacaoException.SemFilme, LocacaoException.SemUsuario {
        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        Date dataRetorno = locacaoService
                .alugarFilme(usuario, filmes)
                .getDataRetorno();
        assertThat(dataRetorno, caiEm(Calendar.MONDAY));
    }
}