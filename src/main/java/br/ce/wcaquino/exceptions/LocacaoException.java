package br.ce.wcaquino.exceptions;

public class LocacaoException {

    public static class SemUsuario extends Exception { }
    public static class SemFilme extends Exception { }
    public static class FilmeSemEstoque extends Exception { }

}
