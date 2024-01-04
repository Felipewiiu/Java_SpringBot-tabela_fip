package br.com.alura.consultafipe.principal;

import br.com.alura.consultafipe.model.Dados;
import br.com.alura.consultafipe.model.Modelos;
import br.com.alura.consultafipe.model.Veiculo;
import br.com.alura.consultafipe.services.ConsomeApi;
import br.com.alura.consultafipe.services.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private ConsomeApi consomeApi = new ConsomeApi();
    private  String ENDERECO = "https://parallelum.com.br/fipe/api/v1/";
    private final String marca = "/marcas";
    private ConverteDados conversor = new ConverteDados();
    private String tipo;

    public void exibeMenu() {
        Scanner leitura = new Scanner(System.in);
        System.out.println("** carros **");
        System.out.println("** motos **");
        System.out.println("** caminhoes **\n");
        System.out.println("Escolha o modelo de veículo na lista acima\n");


        var veiculo = leitura.nextLine();

        System.out.println("Essas são as marcas disponíveis para " + veiculo);
        String json = consomeApi.pegaDados(ENDERECO + veiculo + marca);

        var EnderecoVeiculo = ENDERECO + veiculo + marca;
        List<Dados> marcas = conversor.obterLista(json, Dados.class);

        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                        .forEach(System.out::println);

        System.out.println("Informe o código da marca para a consulta: ");
        var codMarca = leitura.nextLine();
        EnderecoVeiculo = EnderecoVeiculo + "/" + codMarca + "/modelos";
        System.out.println(EnderecoVeiculo);
        json = consomeApi.pegaDados(EnderecoVeiculo);
        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println(modeloLista);

        System.out.println("\nModelos dessa marca: ");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo)).forEach(System.out::println);


        System.out.println("\nDigite um trecho do nome do veiculo a ser buscado");

        var nomeVeiculo = leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList()); // aqui se gera uma nova lista com o collect


        System.out.println("\nModelos filtrados");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite por favor o código do modelo para buscar os valores de avaliação");

        var codigoModelo = leitura.nextLine();

        EnderecoVeiculo = EnderecoVeiculo + "/" + codigoModelo + "/anos";

        json = consomeApi.pegaDados(EnderecoVeiculo);
        List<Dados> anos = conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++){
            var enderecoAnos = EnderecoVeiculo + "/" + anos.get(i).codigo();
            json = consomeApi.pegaDados(enderecoAnos);
            Veiculo veiculoo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculoo);
        }

        System.out.println("Todos os veículo filtrados com avaliação por ano: ");

        veiculos.forEach(System.out::println);

    }
}
