import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MegaDaVirada {

    public static void main(String[] args) {
        String filePath = "mega_sena_da_virada/mega_sena_asloterias_ate_concurso_2809_sorteio.csv"; // Caminho fixo para o arquivo CSV

        Scanner scanner = new Scanner(System.in);

        System.out.println("Quantos jogos você deseja gerar?");
        int quantidadeJogos = scanner.nextInt();

        List<List<Integer>> historico = lerHistorico(filePath);
        if (historico.isEmpty()) {
            System.out.println("Não foi possível carregar o histórico. Verifique o arquivo.");
            scanner.close();
            return;
        }

        Map<Integer, Integer> frequencias = calcularFrequencia(historico);

        System.out.println("Jogos gerados:");
        for (int i = 0; i < quantidadeJogos; i++) {
            List<Integer> jogo = gerarJogo(frequencias);
            System.out.println(jogo);
        }

        scanner.close();
    }

    private static List<List<Integer>> lerHistorico(String filePath) {
        List<List<Integer>> historico = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                // Ignorar linhas que não começam com números (pular cabeçalho)
                if (!linha.matches("^\\d+.*")) {
                    continue;
                }

                String[] colunas = linha.split(",");
                if (colunas.length < 7) continue; // Verifica se há pelo menos 6 números

                List<Integer> sorteio = new ArrayList<>();
                for (int i = 2; i < 8; i++) { // Colunas "bola 1" até "bola 6"
                    sorteio.add(Integer.parseInt(colunas[i].trim()));
                }
                historico.add(sorteio);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
        return historico;
    }

    private static Map<Integer, Integer> calcularFrequencia(List<List<Integer>> historico) {
        Map<Integer, Integer> frequencias = new HashMap<>();
        for (List<Integer> sorteio : historico) {
            for (Integer numero : sorteio) {
                frequencias.put(numero, frequencias.getOrDefault(numero, 0) + 1);
            }
        }
        return frequencias;
    }

    private static List<Integer> gerarJogo(Map<Integer, Integer> frequencias) {
        List<Integer> jogo = new ArrayList<>();

        // Garantir distribuição de pares e ímpares
        int pares = 0, impares = 0;

        // Garantir distribuição por faixas
        int[] faixas = new int[6]; // Contador para faixas 1-10, 11-20, ..., 51-60

        while (jogo.size() < 6) {
            int numero = selecionarNumeroPonderado(frequencias);
            if (jogo.contains(numero)) continue;

            int faixa = (numero - 1) / 10; // Calcula a faixa do número

            if (faixas[faixa] >= 1) continue; // Garante no máximo 1 número por faixa

            if (numero % 2 == 0 && pares < 3) {
                jogo.add(numero);
                pares++;
                faixas[faixa]++;
            } else if (numero % 2 != 0 && impares < 3) {
                jogo.add(numero);
                impares++;
                faixas[faixa]++;
            }
        }

        // Ordena o jogo
        Collections.sort(jogo);
        return jogo;
    }

    private static int selecionarNumeroPonderado(Map<Integer, Integer> frequencias) {
        List<Map.Entry<Integer, Integer>> lista = new ArrayList<>(frequencias.entrySet());

        // Criar uma lista ponderada
        List<Integer> pool = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : lista) {
            int peso = entry.getValue(); // Frequência como peso
            for (int i = 0; i < peso; i++) {
                pool.add(entry.getKey());
            }
        }

        // Selecionar um número aleatório da lista ponderada
        Random random = new Random();
        return pool.get(random.nextInt(pool.size()));
    }
}
