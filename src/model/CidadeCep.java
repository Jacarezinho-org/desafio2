package model;

public record CidadeCep(
        String cidade,
        long cepInicial,
        long cepFinal
) {
    public boolean contemCep (long cep){
        return cep >= cepInicial && cep <= cepFinal;
    }


}
