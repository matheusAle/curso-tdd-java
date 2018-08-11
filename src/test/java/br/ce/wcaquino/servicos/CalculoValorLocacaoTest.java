package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.LocacaoException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

    private LocacaoService locacaoService;
    @Parameterized.Parameter(value = 0)
    public List<Filme> filmes;
    @Parameterized.Parameter(value = 1)
    public double valorLocacao;

    @Parameterized.Parameter(value = 2)
    public String msg;

    @Before
    public void setup() {
        locacaoService = new LocacaoService();
    }

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getParamentros() {
        Filme filme1 = new Filme("Filme 1", 2, 4D);
        Filme filme2 = new Filme("Filme 2", 2, 4D);
        Filme filme3 = new Filme("Filme 3", 2, 4D);
        Filme filme4 = new Filme("Filme 4", 2, 4D);
        Filme filme5 = new Filme("Filme 5", 2, 4D);
        Filme filme6 = new Filme("Filme 6", 2, 4D);
        return Arrays.asList(new Object[][] {
                {Arrays.asList(filme1, filme2, filme3), 11D, "3 filme 25% de desconto"},
                {Arrays.asList(filme1, filme2, filme3, filme4), 13D, "4 filme 50% de desconto"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14D, "5 filme 75% de desconto"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14D, "6 filme 100% de desconto"},
        });
    }

    @Test
    public void deveCalcularOValorDaLocacaoConsiderandoDescontos() throws LocacaoException.FilmeSemEstoque, LocacaoException.SemFilme, LocacaoException.SemUsuario {
        Locacao locacao = locacaoService.alugarFilme(new Usuario("usuario 1"), filmes);
        assertThat(locacao.getValor(), is(equalTo(valorLocacao)));
    }
}
