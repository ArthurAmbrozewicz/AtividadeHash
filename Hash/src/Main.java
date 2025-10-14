import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

// Classe Registro (cada elemento tem um código de 9 dígitos)
class Registro {
    int codigo;

    Registro(int codigo) {
        this.codigo = codigo;
    }
}

/*
 * Função hash multiplicativa (Knuth): h(k) = floor(m * frac(k * A))
 * onde A = (sqrt(5)-1)/2
 */
class HashUtils {
    public static int multiplicativeHash(int chave, int m) {
        double A = (Math.sqrt(5.0) - 1.0) / 2.0; // constante de Knuth ~0.618...
        double frac = (chave * A) % 1.0;
        return (int) Math.floor(m * frac);
    }
}

// ================= Encadeamento com função multiplicativa =================
class HashChainingMultiplicativo {
    private Nodo[] tabela;
    private int tamanho;
    public long colisoes; // use long para contar grandes quantidades

    class Nodo {
        Registro dado;
        Nodo prox;

        Nodo(Registro r) {
            this.dado = r;
        }
    }

    HashChainingMultiplicativo(int tamanho) {
        this.tamanho = tamanho;
        this.tabela = new Nodo[tamanho];
        this.colisoes = 0;
    }

    private int hash(int chave) {
        return HashUtils.multiplicativeHash(chave, tamanho);
    }

    // Inserção ordenada na lista encadeada (ordenar por codigo).
    // Conta colisões ao percorrer a lista até o ponto de inserção.
    public void inserir(Registro r) {
        int pos = hash(r.codigo);
        Nodo novo = new Nodo(r);

        if (tabela[pos] == null) {
            tabela[pos] = novo;
            return;
        }

        // já existe elemento -> colisão inicial
        colisoes++; // colisão por bucket ocupado

        Nodo atual = tabela[pos];
        Nodo anterior = null;

        // inserir ordenado: contar uma colisão por nó visitado ao procurar a posição
        while (atual != null && atual.dado.codigo < r.codigo) {
            colisoes++; // percorrendo a lista conta como colisão conforme enunciado
            anterior = atual;
            atual = atual.prox;
        }

        if (anterior == null) {
            // inserir no início
            novo.prox = tabela[pos];
            tabela[pos] = novo;
        } else {
            novo.prox = anterior.prox;
            anterior.prox = novo;
        }
    }

    public boolean buscar(int codigo) {
        int pos = hash(codigo);
        Nodo atual = tabela[pos];
        while (atual != null) {
            if (atual.dado.codigo == codigo) return true;
            atual = atual.prox;
        }
        return false;
    }

    public int maiorLista() {
        int maior = 0;
        for (Nodo n : tabela) {
            int cont = 0;
            while (n != null) {
                cont++;
                n = n.prox;
            }
            if (cont > maior) maior = cont;
        }
        return maior;
    }

    // calcula gaps no vetor (considerando bucket null vs não-null)
    public int[] calcularGaps() {
        int gapAtual = 0, gapMin = Integer.MAX_VALUE, gapMax = 0, totalGaps = 0, qtdGaps = 0;
        boolean dentro = false;

        for (int i = 0; i < tabela.length; i++) {
            if (tabela[i] == null) {
                gapAtual++;
            } else {
                if (dentro && gapAtual > 0) {
                    gapMin = Math.min(gapMin, gapAtual);
                    gapMax = Math.max(gapMax, gapAtual);
                    totalGaps += gapAtual;
                    qtdGaps++;
                }
                dentro = true;
                gapAtual = 0;
            }
        }

        int gapMedio = (qtdGaps > 0) ? totalGaps / qtdGaps : 0;
        return new int[]{(gapMin == Integer.MAX_VALUE ? 0 : gapMin), gapMax, gapMedio};
    }
}

// ================= Rehashing: Sondagem Quadrática =================
class HashQuadratico {
    private Registro[] tabela;
    private int tamanho;
    public long colisoes;
    private int ocupados;
    private final int c1 = 1;
    private final int c2 = 3;

    HashQuadratico(int tamanho) {
        this.tamanho = tamanho;
        this.tabela = new Registro[tamanho];
        this.colisoes = 0;
        this.ocupados = 0;
    }

    // Função hash inicial usando multiplicativa (evita usar apenas modulo simples)
    private int hash0(int chave) {
        return HashUtils.multiplicativeHash(chave, tamanho);
    }

    public void inserir(Registro r) {
        if (ocupados >= tamanho) return;

        int h0 = hash0(r.codigo);
        int i = 0;
        int pos = -1;

        while (i < tamanho) {
            pos = (h0 + c1 * i + c2 * i * i) % tamanho;
            if (pos < 0) pos += tamanho;
            if (tabela[pos] == null) {
                tabela[pos] = r;
                ocupados++;
                return;
            } else {
                colisoes++; // cada tentativa em posição ocupada conta como colisão
            }
            i++;
        }
        // tabela cheia (não insere)
    }

    public boolean buscar(int codigo) {
        int h0 = hash0(codigo);
        int i = 0;
        while (i < tamanho) {
            int pos = (h0 + c1 * i + c2 * i * i) % tamanho;
            if (pos < 0) pos += tamanho;
            if (tabela[pos] == null) return false;
            if (tabela[pos].codigo == codigo) return true;
            i++;
        }
        return false;
    }

    public int[] calcularGaps() {
        int gapAtual = 0, gapMin = Integer.MAX_VALUE, gapMax = 0, totalGaps = 0, qtdGaps = 0;
        boolean dentro = false;

        for (int i = 0; i < tabela.length; i++) {
            if (tabela[i] == null) {
                gapAtual++;
            } else {
                if (dentro && gapAtual > 0) {
                    gapMin = Math.min(gapMin, gapAtual);
                    gapMax = Math.max(gapMax, gapAtual);
                    totalGaps += gapAtual;
                    qtdGaps++;
                }
                dentro = true;
                gapAtual = 0;
            }
        }

        int gapMedio = (qtdGaps > 0) ? totalGaps / qtdGaps : 0;
        return new int[]{(gapMin == Integer.MAX_VALUE ? 0 : gapMin), gapMax, gapMedio};
    }
}

// ================= Rehashing: Double Hashing (hash duplo) =================
class HashDuploCustom {
    private Registro[] tabela;
    private int tamanho;
    public long colisoes;
    private int ocupados;

    HashDuploCustom(int tamanho) {
        this.tamanho = tamanho;
        this.tabela = new Registro[tamanho];
        this.colisoes = 0;
        this.ocupados = 0;
    }

    // hash1 usando multiplicativa
    private int hash1(int chave) {
        return HashUtils.multiplicativeHash(chave, tamanho);
    }

    // hash2: passo deve ser não-zero e preferencialmente coprimo ao tamanho
    // usamos 1 + (chave % (tamanho - 1)) para garantir [1 .. tamanho-1]
    private int hash2(int chave) {
        int mod = (tamanho > 2) ? (tamanho - 1) : 1;
        return 1 + Math.abs(chave) % mod;
    }

    public void inserir(Registro r) {
        if (ocupados >= tamanho) return;

        int pos = hash1(r.codigo);
        int passo = hash2(r.codigo);
        int tentativas = 0;

        while (tentativas < tamanho) {
            if (tabela[pos] == null) {
                tabela[pos] = r;
                ocupados++;
                return;
            } else {
                colisoes++;
                pos = (pos + passo) % tamanho;
                tentativas++;
            }
        }
        // não inserido se cheio
    }

    public boolean buscar(int codigo) {
        int pos = hash1(codigo);
        int passo = hash2(codigo);
        int tentativas = 0;

        while (tentativas < tamanho) {
            if (tabela[pos] == null) return false;
            if (tabela[pos].codigo == codigo) return true;
            pos = (pos + passo) % tamanho;
            tentativas++;
        }
        return false;
    }

    public int[] calcularGaps() {
        int gapAtual = 0, gapMin = Integer.MAX_VALUE, gapMax = 0, totalGaps = 0, qtdGaps = 0;
        boolean dentro = false;

        for (int i = 0; i < tabela.length; i++) {
            if (tabela[i] == null) {
                gapAtual++;
            } else {
                if (dentro && gapAtual > 0) {
                    gapMin = Math.min(gapMin, gapAtual);
                    gapMax = Math.max(gapMax, gapAtual);
                    totalGaps += gapAtual;
                    qtdGaps++;
                }
                dentro = true;
                gapAtual = 0;
            }
        }

        int gapMedio = (qtdGaps > 0) ? totalGaps / qtdGaps : 0;
        return new int[]{(gapMin == Integer.MAX_VALUE ? 0 : gapMin), gapMax, gapMedio};
    }
}

// ===== CLASSE PRINCIPAL =====
public class Main {
    public static void main(String[] args) throws IOException {
        // Tamanhos das tabelas (escolha exemplo: 1000, 10000, 100000)
        int[] tamanhosTabela = {1000, 10_000, 100_000};
        // Tamanhos dos conjuntos de dados (100k, 1M, 10M)
        int[] tamanhosInsercao = {100_000, 1_000_000, 10_000_000};

        Random random = new Random(42); // seed fixa conforme enunciado

        // CSV para exportação
        FileWriter csv = new FileWriter("resultado_hash_substituido.csv");
        csv.write("Tabela,Hash,TamanhoTabela,NumRegistros,TempoInsercao(ms),Colisoes,GapMin,GapMax,GapMedio,TempoBusca(ms),Encontrados,MaiorLista\n");

        for (int tamanhoTabela : tamanhosTabela) {
            for (int nRegistros : tamanhosInsercao) {
                // Ajuste automático do tamanho da tabela para evitar travamentos extremos
                // (mantive sua ideia original, mas garanta memória suficiente ao rodar)
                int tamanhoTabelaAjustado = Math.max(tamanhoTabela, (int)(nRegistros * 1.5));
                // OBS: para nRegistros muito grandes este ajuste pode demandar MUITA memória.
                // Você pode reduzir tamanhoTabelaAjustado se estiver ficando sem RAM.

                System.out.println("\nTabela ajustada: " + tamanhoTabelaAjustado + " | Registros: " + nRegistros);

                // Gerar registros (usar a mesma seed garante conjuntos reprodutíveis)
                Registro[] registros = new Registro[nRegistros];
                for (int i = 0; i < nRegistros; i++) {
                    registros[i] = new Registro(100_000_000 + random.nextInt(900_000_000));
                }



                // ===== Encadeamento multiplicativo =====
                HashChainingMultiplicativo enc = new HashChainingMultiplicativo(tamanhoTabelaAjustado);
                long inicio = System.currentTimeMillis();
                for (Registro r : registros) enc.inserir(r);
                long tempoInsercao = System.currentTimeMillis() - inicio;

                inicio = System.currentTimeMillis();
                int encontrados = 0;
                for (Registro r : registros) if (enc.buscar(r.codigo)) encontrados++;
                long tempoBusca = System.currentTimeMillis() - inicio;

                int[] gapsEnc = enc.calcularGaps();
                csv.write("Encadeamento,Multiplicativo," + tamanhoTabelaAjustado + "," + nRegistros + "," + tempoInsercao + "," +
                        enc.colisoes + "," + gapsEnc[0] + "," + gapsEnc[1] + "," + gapsEnc[2] + "," +
                        tempoBusca + "," + encontrados + "," + enc.maiorLista() + "\n");

                // ===== Quadrático =====
                HashQuadratico quad = new HashQuadratico(tamanhoTabelaAjustado);
                inicio = System.currentTimeMillis();
                for (Registro r : registros) quad.inserir(r);
                tempoInsercao = System.currentTimeMillis() - inicio;

                inicio = System.currentTimeMillis();
                encontrados = 0;
                for (Registro r : registros) if (quad.buscar(r.codigo)) encontrados++;
                tempoBusca = System.currentTimeMillis() - inicio;

                int[] gapsQuad = quad.calcularGaps();
                csv.write("Rehashing,Quadratico," + tamanhoTabelaAjustado + "," + nRegistros + "," + tempoInsercao + "," +
                        quad.colisoes + "," + gapsQuad[0] + "," + gapsQuad[1] + "," + gapsQuad[2] + "," +
                        tempoBusca + "," + encontrados + ",0\n");

                // ===== Duplo (hash duplo custom) =====
                HashDuploCustom dup = new HashDuploCustom(tamanhoTabelaAjustado);
                inicio = System.currentTimeMillis();
                for (Registro r : registros) dup.inserir(r);
                tempoInsercao = System.currentTimeMillis() - inicio;

                inicio = System.currentTimeMillis();
                encontrados = 0;
                for (Registro r : registros) if (dup.buscar(r.codigo)) encontrados++;
                tempoBusca = System.currentTimeMillis() - inicio;

                int[] gapsDup = dup.calcularGaps();
                csv.write("Rehashing,DoubleHashingCustom," + tamanhoTabelaAjustado + "," + nRegistros + "," + tempoInsercao + "," +
                        dup.colisoes + "," + gapsDup[0] + "," + gapsDup[1] + "," + gapsDup[2] + "," +
                        tempoBusca + "," + encontrados + ",0\n");

                System.out.println("Conjunto processado e salvo no CSV.");
            }

        }

        csv.close();
        System.out.println("\n✅ Testes concluídos! Resultado exportado para resultado_hash_substituido.csv");
    }
}
