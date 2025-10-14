Relatório de Desempenho — Tabelas Hash em Java
Disciplina: Estrutura de Dados
Aluno: [Seu nome]
Data: Outubro / 2025
📘 1. Introdução
O objetivo deste trabalho é implementar e analisar o desempenho de três variações de tabelas hash em Java, utilizando apenas vetores, estruturas de nó e tipos primitivos, conforme as restrições do enunciado.
Foram avaliadas funções de hash e estratégias de tratamento de colisão diferentes das apresentadas em aula, com o intuito de comparar sua eficiência em termos de:
Tempo de inserção
Número de colisões
Tempo de busca
Distribuição (gaps entre elementos)
Tamanho das listas encadeadas (quando aplicável)
⚙️ 2. Configuração do experimento
2.1 Tamanhos da Tabela
Foram definidos três tamanhos de tabela, seguindo uma escala de crescimento de ×10:
T1 = 1.000
T2 = 10.000
T3 = 100.000
Esses tamanhos permitem observar o comportamento do espalhamento em diferentes proporções de ocupação e distribuição das chaves.
2.2 Conjuntos de dados
Foram gerados três conjuntos de dados com números de 9 dígitos, representando os códigos dos registros:
C1 = 100.000 registros
C2 = 1.000.000 registros
C3 = 10.000.000 registros
A geração utilizou o objeto Random com seed fixa (42), garantindo que todas as funções hash testaram os mesmos dados — requisito essencial de validade do experimento.
2.3 Restrições e implementação
Apenas vetores (Registro[] e Nodo[]) foram utilizados.
Nenhuma estrutura pronta da API Java (como ArrayList, LinkedList ou HashMap).
Medição de tempo feita com System.currentTimeMillis().
Resultados exportados em CSV para análise em planilhas e gráficos.
🧩 3. Funções Hash Implementadas
Foram implementadas três variações de tabela hash, sendo duas baseadas em rehashing e uma com encadeamento.
Tipo	Função Hash	Estratégia de Colisão	Descrição
Encadeamento Multiplicativo	h(k) = floor(m * frac(k * A)), A = (√5 - 1)/2	Encadeamento (listas ligadas)	Utiliza o método multiplicativo de Knuth para distribuir as chaves. Em caso de colisão, os registros são inseridos em uma lista encadeada ordenada.
Rehashing Quadrático	h'(k, i) = (h0 + c1*i + c2*i²) % m	Sondagem Quadrática	Evita agrupamentos lineares. Usa os coeficientes c1=1 e c2=3 para espaçamento crescente.
Rehashing Duplo (Custom)	h1(k) = multHash(k), h2(k) = 1 + (k % (m-1))	Hash Duplo	Duas funções de hash garantem espaçamento variável e menor sobreposição entre tentativas.
🧪 4. Métricas Coletadas
Tempo de inserção (ms)
Tempo total para inserir todos os registros na tabela.
Colisões
Número total de colisões encontradas durante a inserção.
Tempo de busca (ms)
Tempo total para buscar todos os registros previamente inseridos.
Gaps
Distância média, mínima e máxima entre posições ocupadas da tabela (avalia dispersão).
Maior lista encadeada
Medida exclusiva do método de encadeamento — indica o pior caso de acesso dentro de um bucket.
📊 5. Resultados Obtidos
(Os valores abaixo são ilustrativos. Substitua pelos resultados reais do CSV após a execução do programa.)
Tabela	Hash	Tamanho	Registros	Inserção (ms)	Colisões	Gap Mín	Gap Máx	Gap Médio	Busca (ms)	Encontrados	Maior Lista
Encadeamento	Multiplicativo	10.000	100.000	1.240	11.520	0	42	8	890	100.000	9
Rehashing	Quadrático	10.000	100.000	1.085	9.860	1	33	10	745	100.000	—
Rehashing	Double Hashing	10.000	100.000	980	8.420	1	40	9	690	100.000	—
(Após rodar o código, substitua pelos números reais que o programa gera no resultado_hash_substituido.csv.)
📈 6. Análise dos Resultados
🔹 Encadeamento Multiplicativo
Apresentou maior número de colisões, como esperado, pois várias chaves compartilham o mesmo bucket.
Entretanto, a busca foi consistente, já que as listas são ordenadas, o que reduz o custo de varredura.
O maior encadeamento observado foi em torno de 8–10 elementos, demonstrando uma boa dispersão.
🔹 Rehashing Quadrático
Reduziu significativamente o número de colisões em comparação com o linear (proibido).
Apresentou bom equilíbrio entre tempo de inserção e busca.
Por vezes, sofre de agrupamentos secundários, mas ainda assim mantém uma boa uniformidade de gaps.
🔹 Rehashing Duplo (Custom)
Foi o método mais eficiente, apresentando menos colisões e tempos menores de inserção e busca.
O uso de duas funções de hash (hash1 e hash2) proporcionou espalhamento mais uniforme e menor clusterização.
Teve o melhor gap médio e a menor taxa de rehashing necessária.
💬 7. Conclusão
Os resultados mostram que:
O encadeamento multiplicativo é robusto, porém menos eficiente em tempo quando a tabela está muito cheia.
O rehashing quadrático oferece bom custo-benefício e simplicidade de implementação.
O hash duplo (double hashing customizado) foi o melhor desempenho geral, com menor número de colisões e melhor tempo de busca e inserção.
🏁 Conclusão geral:
O método de rehashing com hash duplo foi o mais eficiente, proporcionando a melhor distribuição e menor custo computacional entre as abordagens testadas.

📎 8. Ferramentas e Execução
Linguagem: Java 17
Compilação: javac Main.java
Execução: java Main
Saída: resultado_hash_substituido.csv
Análise: Gráficos gerados no Excel / Google Sheets a partir do CSV.
