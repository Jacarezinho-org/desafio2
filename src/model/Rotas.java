package model;

import java.util.ArrayList;
import java.util.List;

public class Rotas {
    private final List<ConexaoCidade> rotas = new ArrayList<>();

    public void adicionarRota(ConexaoCidade conexao) {
        rotas.add(conexao);
    }

    public List<ConexaoCidade> listarRotas() {
        return new ArrayList<>(rotas);
    }

    public boolean rotaExiste(String cidadeOrigem, String cidadeDestino) {
        for (ConexaoCidade rota : rotas) {
            if (rota.cidadeOrigem().equals(cidadeOrigem) && rota.cidadeDestino().equals(cidadeDestino)) {
                return true;
            }
        }
        return false;
    }
}
