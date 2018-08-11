package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.LocacaoException;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.*;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;

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
        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
        error.checkThat(locacao.getValor(), is(equalTo(4.0 * filmes.size())));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
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
    public void devaPagarApenas25PorcentoNoTerceiroFilme() throws LocacaoException.FilmeSemEstoque, LocacaoException.SemFilme, LocacaoException.SemUsuario {
        this.filmes.add(new Filme("Filme 3", 2, 4.0));
        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
        // 4 + 4 + 3 (25%) = 11D
        Assert.assertThat(locacao.getValor(), is(equalTo(11D)));
    }

    @Test
    public void devaPagarApenas50PorcentoNoQuartoFilme() throws LocacaoException.FilmeSemEstoque, LocacaoException.SemFilme, LocacaoException.SemUsuario {
        this.filmes.add(new Filme("Filme 3", 2, 4.0));
        this.filmes.add(new Filme("Filme 4", 2, 4.0));
        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
        // 4 + 4 + 3 (25%) + 2 (50%) = 13D
        Assert.assertThat(locacao.getValor(), is(equalTo(13D)));
    }

    @Test
    public void devaPagarApenas75PorcentoNoQuintoFilme() throws LocacaoException.FilmeSemEstoque, LocacaoException.SemFilme, LocacaoException.SemUsuario {
        this.filmes.add(new Filme("Filme 3", 2, 4.0));
        this.filmes.add(new Filme("Filme 4", 2, 4.0));
        this.filmes.add(new Filme("Filme 5", 2, 4.0));
        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
        // 4 + 4 + 3 (25%) + 2 (50%) + 1 (75%) = 14D
        Assert.assertThat(locacao.getValor(), is(equalTo(14D)));
    }

    @Test
    public void devaPagarApenas100PorcentoNoSextoFilme() throws LocacaoException.FilmeSemEstoque, LocacaoException.SemFilme, LocacaoException.SemUsuario {
        this.filmes.add(new Filme("Filme 4", 2, 4.0));
        this.filmes.add(new Filme("Filme 5", 2, 4.0));
        this.filmes.add(new Filme("Filme 6", 2, 4.0));
        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
        // 4 + 4 + 3 (25%) + 2 (50%) + 1 (75%) + 0 (100%)= 14D
        Assert.assertThat(locacao.getValor(), is(equalTo(14D)));
    }

}