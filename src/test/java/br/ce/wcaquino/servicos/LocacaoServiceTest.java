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
        filmes.add(new Filme("Filme 1", 1, 5.0));
        filmes.add(new Filme("Filme 2", 2, 6.0));
        filmes.add(new Filme("Filme 3", 2, 7.0));
    }

    @Test()
    public void testeLocacao() throws Exception {
        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
        error.checkThat(locacao.getValor(), is(equalTo(filmes.stream().mapToDouble(Filme::getPrecoLocacao).sum())));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
    }

    @Test
    public void testLocacao_filmeSemEstoque() throws Exception {
        exception.expect(LocacaoException.FilmeSemEstoque.class);
        filmes.get(0).setEstoque(0);
        locacaoService.alugarFilme(usuario, filmes);
    }

    @Test
    public void testLocacao_semUsuario() throws Exception {
        exception.expect(LocacaoException.SemUsuario.class);
        locacaoService.alugarFilme(null, filmes);
    }

    @Test
    public void testLocacao_semFilme_null() throws Exception {
        exception.expect(LocacaoException.SemFilme.class);
        locacaoService.alugarFilme(usuario, null);
    }

    @Test
    public void testLocacao_semFilme_ListaVazia() throws Exception {
        exception.expect(LocacaoException.SemFilme.class);
        locacaoService.alugarFilme(usuario, new ArrayList<Filme>());
    }
}