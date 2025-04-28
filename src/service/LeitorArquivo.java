package service;

import model.CidadeCep;
import model.ConexaoCidade;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LeitorArquivo {

    public static class ResultadoLeitura {
        public final List<CidadeCep> cidades;
        public final List<ConexaoCidade> conexoes;
        public final String cepOrigem;
        public final String cepDestino;

        public ResultadoLeitura(List<CidadeCep> cidades, List<ConexaoCidade> conexoes, String cepOrigem, String cepDestino) {
            this.cidades = cidades;
            this.conexoes = conexoes;
            this.cepOrigem = cepOrigem;
            this.cepDestino = cepDestino;
        }
    }

    public ResultadoLeitura lerArquivo(String caminho) throws IOException {
        List<CidadeCep> cidades = new ArrayList<>();
        List<ConexaoCidade> conexoes = new ArrayList<>();
        String cepOrigem = null;
        String cepDestino = null;
        int separador = 0;

        try (BufferedReader arquivo = new BufferedReader(new FileReader(caminho))) {
            String linha;
            while ((linha = arquivo.readLine()) != null) {
                linha = linha.trim();

                if (linha.equals("--")) {
                    separador++;
                    continue;
                }

                if (separador == 0) {
                    String[] partes = linha.split(",");
                    if (partes.length == 3) {
                        cidades.add(new CidadeCep(
                                partes[0].trim(),
                                Long.parseLong(partes[1].trim()),
                                Long.parseLong(partes[2].trim())
                        ));
                    }
                } else if (separador == 1) {
                    String[] partes = linha.split(",");
                    if (partes.length == 3) {
                        conexoes.add(new ConexaoCidade(
                                partes[0].trim(),
                                partes[1].trim(),
                                Double.parseDouble(partes[2].trim())
                        ));
                    }
                } else if (separador == 2) {
                    if (linha.contains(",")) {
                        String[] partes = linha.split(",");
                        if (partes.length == 2) {
                            cepOrigem = partes[0].trim();
                            cepDestino = partes[1].trim();
                        } else {
                            throw new IOException("Formato inválido na seção de CEPs. Esperado dois valores separados por vírgula.");
                        }
                    } else {
                        throw new IOException("Formato inválido na seção de CEPs. Esperado dois valores separados por vírgula.");
                    }
                }
            }
        }

        if (separador < 2 || cepOrigem == null || cepDestino == null) {
            throw new IOException("Formato de arquivo inválido. Verifique se contém seções separadas por '--' e os dois CEPs finais da busca.");
        }

        return new ResultadoLeitura(cidades, conexoes, cepOrigem, cepDestino);
    }
}
