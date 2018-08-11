package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.LocacaoException;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.Date;

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
    private Filme filme;

    @Before
    public void setup() {
        locacaoService = new LocacaoService();
        usuario = new Usuario("Usuario 1");
        filme = new Filme("Filme 1", 2, 5.0);
    }

    @Test()
    public void testeLocacao() throws Exception {
        Locacao locacao = locacaoService.alugarFilme(usuario, filme);
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
    }

    @Test
    public void testLocacao_filmeSemEstoque() throws Exception {
        exception.expect(LocacaoException.FilmeSemEstoque.class);
        filme.setEstoque(0);
        locacaoService.alugarFilme(usuario, filme);
    }

    @Test
    public void testLocacao_semUsuario() throws Exception {
        exception.expect(LocacaoException.SemUsuario.class);
        locacaoService.alugarFilme(null, filme);
    }

    @Test
    public void testLocacao_semFilme() throws Exception {
        exception.expect(LocacaoException.SemFilme.class);
        locacaoService.alugarFilme(usuario, null);

    }
}