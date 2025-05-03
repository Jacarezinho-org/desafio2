import service.LeitorArquivo;
import service.BuscarCaminho;

import java.io.IOException;
import java.util.Scanner;

/*
Nome: Vitor
JDK: MS 21
IDE: IntelliJ
*/
public class Main {
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);

        System.out.println("======================================");
        System.out.println("        Busca de Caminho por CEP      ");
        System.out.println("======================================");
        System.out.println("Informe o caminho completo do arquivo:");
        System.out.println("Exemplo: C:\\Users\\usuario\\arquivo.txt");
        System.out.print("Digite o caminho: ");
        String arquivo = entrada.nextLine().trim();

        try {
            LeitorArquivo leitor = new LeitorArquivo();
            LeitorArquivo.ResultadoLeitura resultado = leitor.lerArquivo(arquivo);

            BuscarCaminho buscar = new BuscarCaminho(
                    resultado.cidades,
                    resultado.conexoes,
                    resultado.cepOrigem,
                    resultado.cepDestino
            );

            buscar.executarBusca();

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ocorreu um erro inesperado: " + e.getMessage());
        }
    }
}
