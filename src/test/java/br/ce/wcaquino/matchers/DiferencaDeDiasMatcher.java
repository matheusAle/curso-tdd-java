package br.ce.wcaquino.matchers;

import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Date;

public class DiferencaDeDiasMatcher extends TypeSafeMatcher<Date> {

    private Integer diferencoDeDias;

    public DiferencaDeDiasMatcher(Integer diferencoDeDias) {
        this.diferencoDeDias = diferencoDeDias;
    }

    @Override
    protected boolean matchesSafely(Date date) {
        return DataUtils.isMesmaData(
                date,
                DataUtils.obterDataComDiferencaDias(diferencoDeDias)
        );
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(DataUtils.obterDataComDiferencaDias(diferencoDeDias).toString());
    }
}
