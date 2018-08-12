package br.ce.wcaquino.servicos;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.LocacaoException;
import br.ce.wcaquino.exceptions.SPCException;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.*;

import java.util.*;

import static br.ce.wcaquino.matchers.AppMatchers.caiEm;
import static br.ce.wcaquino.matchers.AppMatchers.ehHoje;
import static br.ce.wcaquino.matchers.AppMatchers.ehHojeComDiferencaDeDias;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocacaoServiceTest {

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @InjectMocks @Spy
    public LocacaoService locacaoService;

    @Mock
    public SPCService spcService;

    @Mock
    public LocacaoDAO locacaoDAO;

    @Mock
    public EmailService emailService;

    private List<Filme> filmes;
    private Usuario usuario;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        usuario = UsuarioBuilder.umUsuario().agora();
        filmes = Arrays.asList(FilmeBuilder.umFilme().agora(), FilmeBuilder.umFilme().agora());
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
    public void deveLancarExcecaoAoTentarAlugarFilmesParausuarioNogativavadosNoSPC() throws Exception {
        exception.expect(LocacaoException.UsuarioNegativoNoSPC.class);
        Mockito.when(spcService.possuiNegativacao(usuario)).thenReturn(true);
        locacaoService.alugarFilme(usuario, filmes);
    }

    @Test
    public void deveEnviarEmailparaUsuariosComLocoesAtrasadas() throws Exception {
        Usuario usuario1 = UsuarioBuilder.umUsuario().comNome("usuario atrasado 1").agora();
        Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("usuario em dia").agora();
        Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("usuario atrasado 2").agora();
        List<Locacao> locacaos = Arrays.asList(
                LocacaoBuilder.umLocacao().comUsuario(usuario1).atrasada().agora(),
                LocacaoBuilder.umLocacao().comUsuario(usuario2).agora(),
                LocacaoBuilder.umLocacao().comUsuario(usuario3).atrasada().agora());

        Mockito.when(locacaoDAO.obterLocacoesPendentes()).thenReturn(locacaos);

        locacaoService.notificarAtrasos();

        verify(emailService).notificarAtraso(usuario1);
        verify(emailService, never()).notificarAtraso(usuario2);
        verify(emailService).notificarAtraso(usuario3);
    }

    @Test
    public void deveTratarErroNoSPC() throws Exception {
        when(spcService.possuiNegativacao(usuario)).thenThrow(new SPCException.ErroInesperado());
        exception.expect(SPCException.ErroInesperado.class);
        locacaoService.alugarFilme(usuario, filmes);
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarFilmeNoSabado() throws Exception {
        Mockito.doReturn(DataUtils.obterData(11, 8, 2018))
                .when(locacaoService).obterData();

        Date dataRetorno = locacaoService
                .alugarFilme(usuario, filmes)
                .getDataRetorno();

        assertThat(dataRetorno, caiEm(Calendar.MONDAY));
    }

    @Test()
    public void deveAlugarFilme() throws Exception {
        Mockito.doReturn(DataUtils.obterData(9, 8, 2018))
                .when(locacaoService).obterData();

        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

        error.checkThat(locacao.getValor(), is(equalTo(4.0 * filmes.size())));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(9, 8, 2018)), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(10, 8, 2018)), is(true));
    }

    @Test
    public void deveProrrogarUmaLocacao() throws Exception {
        Locacao locacao = LocacaoBuilder.umLocacao().agora();
        locacaoService.prorrogarAlocacao(locacao, 3);

        ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(locacaoDAO).salvar(argumentCaptor.capture());
        Locacao locacaoRetornada = argumentCaptor.getValue();

        error.checkThat(locacaoRetornada.getValor(), is(12D));
        error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
        error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDeDias(3));
    }

}