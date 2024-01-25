package com.github.com.GabrielVitor3.TabelaFipe.principal;

import com.github.com.GabrielVitor3.TabelaFipe.model.Dados;
import com.github.com.GabrielVitor3.TabelaFipe.model.Modelos;
import com.github.com.GabrielVitor3.TabelaFipe.model.Veiculo;
import com.github.com.GabrielVitor3.TabelaFipe.service.ConsumoApi;
import com.github.com.GabrielVitor3.TabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados converte = new ConverteDados();
    private Scanner sc = new Scanner(System.in);

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu() {
        var menu = """
                *** OPÇÕES ***
                Carro
                Moto    
                Caminhão
                                
                Digite uma das opções para consultar:
                """;

        System.out.println(menu);

        var opcao = sc.nextLine();
        String endereco;

        if (opcao.toLowerCase().contains("carr")) {
            endereco = URL_BASE + "carros/marcas";
        } else if (opcao.toLowerCase().contains("mot")) {
            endereco = URL_BASE + "motos/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/mmarcas";
        }

        var json = consumo.obterDados(endereco);
        System.out.println(json);
        var marcas = converte.obterLista(json, Dados.class);
        marcas.stream().sorted(Comparator.comparing(Dados::codigo)).forEach(System.out::println);

        System.out.println("Informe o código da marca para consulta: ");
        var codigomarca = sc.nextLine();

        endereco = endereco + "/" + codigomarca + "/modelos";
        json = consumo.obterDados(endereco);
        var modeloLista = converte.obterDados(json, Modelos.class);


        System.out.println("\nModelos dessa marca: ");
        modeloLista.modelos().stream().sorted(Comparator.comparing(Dados::codigo)).forEach(System.out::println);

        System.out.println("\n Digite um trecho do nome do carro a ser buscado");
        var nomeVeiculo = sc.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrador");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite o código do modelo para buscar os valores");
        var codigoModelo = sc.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);
        List<Dados> anos = converte.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for(int i = 0; i < anos.size(); i++){
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = converte.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veiculos filtrados com avaliações por ano: ");
        veiculos.forEach(System.out::println);


    }
}
