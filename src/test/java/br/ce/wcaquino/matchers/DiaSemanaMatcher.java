package br.ce.wcaquino.matchers;

import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// O paramentro ge nerico reverece ao tipo do paramentro 'actual' da função assertThat
public class DiaSemanaMatcher extends TypeSafeMatcher<Date> {

    private Integer diaSemana;


    public DiaSemanaMatcher(Integer diaSemana) {
        this.diaSemana = diaSemana;
    }

    // metodo de execulta o teste.
    @Override
    protected boolean matchesSafely(Date date) {
        return DataUtils.verificarDiaSemana(date, diaSemana);
    }

    @Override
    public void describeTo(Description description) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, diaSemana);
        String dataExtenso = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
        description.appendText(dataExtenso);

    }
}
