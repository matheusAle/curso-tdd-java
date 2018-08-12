package br.ce.wcaquino.matchers;

public class AppMatchers {

    public static DiaSemanaMatcher caiEm(Integer diaSemana) {
        return new DiaSemanaMatcher(diaSemana);
    }

    public static DiferencaDeDiasMatcher ehHojeComDiferencaDeDias(Integer diaSemana) {
        return new DiferencaDeDiasMatcher(diaSemana);
    }

    public static DiferencaDeDiasMatcher ehHoje() {
        return new DiferencaDeDiasMatcher(0);
    }
}
