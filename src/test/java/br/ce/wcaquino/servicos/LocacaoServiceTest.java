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

    @Test()
    public void testeLocacao() throws Exception {
        Locacao locacao = new LocacaoService().alugarFilme(
                new Usuario("Usuario 1"),
                new Filme("Filme 1", 2, 5.0)
        );

        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
    }

    @Test
    public void testLocacao_filmeSemEstoque() throws Exception {
        exception.expect(LocacaoException.FilmeSemEstoque.class);
        new LocacaoService().alugarFilme(
                new Usuario("Usuario 1"),
                new Filme("Filme 1", 0, 5.0)
        );
    }

    @Test
    public void testLocacao_semUsuario() throws Exception {
        exception.expect(LocacaoException.SemUsuario.class);
        new LocacaoService().alugarFilme(
                null,
                new Filme("Filme 1", 0, 5.0)
        );
    }

    @Test
    public void testLocacao_semFilme() throws Exception {
        exception.expect(LocacaoException.SemFilme.class);
        new LocacaoService().alugarFilme(
                new Usuario("Usuario 1"),
                null
        );
    }
}