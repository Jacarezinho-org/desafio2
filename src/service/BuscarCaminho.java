package service;

import model.CidadeCep;
import model.ConexaoCidade;

import java.util.*;

public class BuscarCaminho {

    private final List<CidadeCep> cidades;
    private final List<ConexaoCidade> conexoes;
    private final String cepOrigem;
    private final String cepDestino;
    private final Map<String, List<ConexaoCidade>> grafo = new HashMap<>();

    public BuscarCaminho(List<CidadeCep> cidades, List<ConexaoCidade> conexoes, String cepOrigem, String cepDestino) {
        this.cidades = cidades;
        this.conexoes = conexoes;
        this.cepOrigem = cepOrigem;
        this.cepDestino = cepDestino;
        construirGrafo();
    }

    private void construirGrafo() {
        for (ConexaoCidade conexao : conexoes) {
            grafo.computeIfAbsent(conexao.cidadeOrigem(), k -> new ArrayList<>()).add(conexao);
            grafo.computeIfAbsent(conexao.cidadeDestino(), k -> new ArrayList<>()).add(
                    new ConexaoCidade(conexao.cidadeDestino(), conexao.cidadeOrigem(), conexao.custo())
            );
        }
    }

    private String acharCidade(String cepStr) {
        long cep;
        try {
            cep = Long.parseLong(cepStr);
        } catch (NumberFormatException e) {
            return null;
        }

        for (CidadeCep cidade : cidades) {
            if (cidade.contemCep(cep)) {
                return cidade.cidade();
            }
        }
        return null;
    }

    public void executarBusca() {
        String cidadeAtual = acharCidade(cepOrigem);
        String cidadeDestino = acharCidade(cepDestino);

        if (cidadeAtual == null || cidadeDestino == null) {
            System.out.println("CEP de origem ou destino não encontrado.");
            return;
        }

        List<String> caminho = buscarMenorCaminho(cidadeAtual, cidadeDestino);

        if (caminho == null || caminho.isEmpty()) {
            System.out.println("Não foi possível encontrar um caminho entre as cidades.");
            return;
        }

        double custoTotal = 0.0;
        for (int i = 0; i < caminho.size() - 1; i++) {
            String origem = caminho.get(i);
            String destino = caminho.get(i + 1);

            boolean encontrouConexao = false;
            for (ConexaoCidade conexao : conexoes) {
                if (conexao.cidadeOrigem().equals(origem) && conexao.cidadeDestino().equals(destino)) {
                    custoTotal += conexao.custo();
                    encontrouConexao = true;
                    break;
                }
            }

            // se não achou diretamente, tenta invertido
            if (!encontrouConexao) {
                for (ConexaoCidade conexao : conexoes) {
                    if (conexao.cidadeOrigem().equals(destino) && conexao.cidadeDestino().equals(origem)) {
                        custoTotal += conexao.custo();
                        break;
                    }
                }
            }
        }

        System.out.println("\nOrigem: " + cidadeAtual);
        System.out.println("Destino: " + cidadeDestino);
        System.out.println("Caminho encontrado:");
        for (String cidade : caminho) {
            System.out.println(cidade);
        }
        System.out.printf("\nCusto total do caminho: R$ %.2f\n", custoTotal);
    }

    private List<String> buscarMenorCaminho(String origem, String destino) {
        Map<String, Double> distancias = new HashMap<>();
        Map<String, String> anteriores = new HashMap<>();
        PriorityQueue<EntradaFila> fila = new PriorityQueue<>(Comparator.comparingDouble(e -> e.distancia));

        for (String cidade : grafo.keySet()) {
            distancias.put(cidade, Double.POSITIVE_INFINITY);
        }
        distancias.put(origem, 0.0);
        fila.add(new EntradaFila(origem, 0.0));

        while (!fila.isEmpty()) {
            EntradaFila atual = fila.poll();
            String cidadeAtual = atual.cidade;

            if (cidadeAtual.equals(destino)) {
                break;
            }

            for (ConexaoCidade conexao : grafo.getOrDefault(cidadeAtual, Collections.emptyList())) {
                double novaDistancia = distancias.get(cidadeAtual) + conexao.custo();
                if (novaDistancia < distancias.getOrDefault(conexao.cidadeDestino(), Double.POSITIVE_INFINITY)) {
                    distancias.put(conexao.cidadeDestino(), novaDistancia);
                    anteriores.put(conexao.cidadeDestino(), cidadeAtual);
                    fila.add(new EntradaFila(conexao.cidadeDestino(), novaDistancia));
                }
            }
        }

        List<String> caminho = new LinkedList<>();
        String atual = destino;

        while (atual != null) {
            caminho.add(0, atual);
            atual = anteriores.get(atual);
        }

        if (!caminho.isEmpty() && caminho.get(0).equals(origem)) {
            return caminho;
        } else {
            return Collections.emptyList();
        }
    }

    private static class EntradaFila {
        private final String cidade;
        private final double distancia;

        public EntradaFila(String cidade, double distancia) {
            this.cidade = cidade;
            this.distancia = distancia;
        }
    }
}
