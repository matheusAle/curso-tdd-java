package br.ce.wcaquino.suites;

import br.ce.wcaquino.servicos.CalculoValorLocacaoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LocacaoServiceTest.class,
        CalculoValorLocacaoTest.class
})
public class SuiteExecucao {
}
