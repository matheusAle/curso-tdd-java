package br.ce.wcaquino.servicos;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.LocacaoException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        locacaoService.setSpcService(Mockito.mock(SPCService.class));
        locacaoService.setDao(Mockito.mock(LocacaoDAO.class));
    }

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getParamentros() {
        List<Filme> filmes = IntStream
                .range(0, 7)
                .mapToObj(i -> FilmeBuilder.umFilme().agora())
                .collect(Collectors.toList());
        return Arrays.asList(new Object[][] {
                {filmes.subList(0, 3), 11D, "3 filme 25% de desconto"},
                {filmes.subList(0, 4), 13D, "4 filme 50% de desconto"},
                {filmes.subList(0, 5), 14D, "5 filme 75% de desconto"},
                {filmes.subList(0, 6), 14D, "6 filme 100% de desconto"},
        });
    }

    @Test
    public void deveCalcularOValorDaLocacaoConsiderandoDescontos() throws LocacaoException.FilmeSemEstoque, LocacaoException.SemFilme, LocacaoException.SemUsuario, LocacaoException.UsuarioNegativoNoSPC {
        Locacao locacao = locacaoService.alugarFilme(UsuarioBuilder.umUsuario().agora(), filmes);
        assertThat(locacao.getValor(), is(equalTo(valorLocacao)));
    }
}
