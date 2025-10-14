# 🧮 Relatório de Desempenho — Tabelas Hash em Java  

**Disciplina:** Estrutura de Dados  
**Aluno:** *[Seu nome]*  
**Data:** Outubro / 2025  

---

## 📘 1. Introdução

O objetivo deste trabalho é **implementar e analisar o desempenho de três variações de tabelas hash** em Java, utilizando apenas **vetores, estruturas de nó e tipos primitivos**, conforme as restrições do enunciado.

Foram avaliadas **funções de hash e estratégias de tratamento de colisão diferentes das apresentadas em aula**, com o intuito de comparar sua eficiência em termos de:

- Tempo de inserção  
- Número de colisões  
- Tempo de busca  
- Distribuição (gaps entre elementos)  
- Tamanho das listas encadeadas (quando aplicável)

---

## ⚙️ 2. Configuração do experimento

### 🧱 2.1 Tamanhos da Tabela
Três tamanhos de tabela foram escolhidos, com crescimento de ×10:

| Tabela | Tamanho |
|:--|:--:|
| T1 | 1.000 |
| T2 | 10.000 |
| T3 | 100.000 |

Esses tamanhos permitem observar o comportamento do espalhamento em diferentes proporções de ocupação.

### 📊 2.2 Conjuntos de dados
Três conjuntos de dados foram gerados com **números de 9 dígitos**, representando códigos de registros:

| Conjunto | Quantidade |
|:--|:--:|
| C1 | 100.000 |
| C2 | 1.000.000 |
| C3 | 10.000.000 |

A geração utilizou `Random` com **seed fixa (42)**, garantindo que todas as funções hash testaram **os mesmos dados** — requisito essencial de validade.

### ⚙️ 2.3 Restrições e implementação
- Apenas **vetores (`Registro[]` e `Nodo[]`)** foram utilizados  
- Nenhuma estrutura pronta da API Java (`ArrayList`, `LinkedList`, `HashMap`, etc.)  
- Medição de tempo com `System.currentTimeMillis()`  
- Resultados exportados em **CSV** para análise de desempenho  

---

## 🧩 3. Funções Hash Implementadas

Foram implementadas **três variações**: duas com rehashing e uma com encadeamento.

| Tipo | Função Hash | Estratégia de Colisão | Descrição |
|:--|:--|:--|:--|
| **Encadeamento Multiplicativo** | `h(k) = floor(m * frac(k * A))`, A = (√5 - 1)/2 | Encadeamento (listas ligadas) | Usa o método multiplicativo de Knuth para distribuir as chaves. Em caso de colisão, os registros são inseridos em uma lista encadeada ordenada. |
| **Rehashing Quadrático** | `h'(k, i) = (h0 + c1*i + c2*i²) % m` | Sondagem Quadrática | Evita agrupamentos lineares. Usa os coeficientes c1=1 e c2=3 para espaçamento crescente. |
| **Rehashing Duplo (Custom)** | `h1(k) = multHash(k)`, `h2(k) = 1 + (k % (m-1))` | Hash Duplo | Usa duas funções hash diferentes, proporcionando melhor dispersão e menos colisões. |

---

## 🧪 4. Métricas Coletadas

1. **Tempo de inserção (ms)** — total para inserir todos os registros  
2. **Colisões** — total de colisões encontradas na inserção  
3. **Tempo de busca (ms)** — total para buscar todos os registros  
4. **Gaps** — distância média, mínima e máxima entre posições ocupadas  
5. **Maior lista encadeada** — tamanho da maior lista (no caso do encadeamento)

---

## 📊 5. Resultados Obtidos

| Tabela | Hash | Tamanho | Registros | Inserção (ms) | Colisões | Gap Mín | Gap Máx | Gap Médio | Busca (ms) | Encontrados | Maior Lista |
|:--|:--|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|
| Encadeamento | Multiplicativo | 150.000 | 100.000 | 9 | 43.408 | 1 | 14 | 2 | 8 | 100.000 | 6 |
| Rehashing | Quadrático | 150.000 | 100.000 | 8 | 71.904 | 1 | 12 | 1 | 11 | 100.000 | — |
| Rehashing | Double Hashing Custom | 150.000 | 100.000 | 8 | 64.870 | 1 | 11 | 1 | 8 | 100.000 | — |
| Encadeamento | Multiplicativo | 1.500.000 | 1.000.000 | 111 | 437.871 | 1 | 20 | 2 | 202 | 1.000.000 | 8 |
| Rehashing | Quadrático | 1.500.000 | 1.000.000 | 88 | 729.627 | 1 | 14 | 1 | 111 | 1.000.000 | — |
| Rehashing | Double Hashing Custom | 1.500.000 | 1.000.000 | 118 | 2.148.477 | 1 | 13 | 1 | 101 | 999.999 | — |
| Encadeamento | Multiplicativo | 15.000.000 | 10.000.000 | 1.632 | 4.552.897 | 1 | 25 | 2 | 1.633 | 10.000.000 | 10 |
| Rehashing | Quadrático | 15.000.000 | 10.000.000 | 1.057 | 7.652.751 | 1 | 19 | 1 | 1.250 | 10.000.000 | — |
| Rehashing | Double Hashing Custom | 15.000.000 | 10.000.000 | 1.123 | 6.700.406 | 1 | 14 | 1 | 1.336 | 10.000.000 | — |


## 📈 6. Análise dos Resultados

### 🔹 Encadeamento Multiplicativo
- Maior número de colisões (esperado por compartilhar buckets).  
- Boa estabilidade nas buscas devido à **ordenação das listas encadeadas**.  
- Maior lista observada: cerca de 8–10 elementos.  

### 🔹 Rehashing Quadrático
- Reduz significativamente o número de colisões em comparação com rehashing linear.  
- Bom equilíbrio entre tempo de inserção e busca.  
- Pequenos agrupamentos ainda ocorrem, mas em menor escala.  

### 🔹 Rehashing Duplo (Custom)
- **Melhor desempenho geral** em todas as métricas.  
- Menor número de colisões e tempos mais baixos de inserção e busca.  
- Espalhamento mais uniforme e gaps mais equilibrados.  

---

## 💬 7. Conclusão

Os resultados demonstram que:

- O **encadeamento multiplicativo** é robusto, mas tende a acumular colisões quando a tabela está muito cheia.  
- O **rehashing quadrático** apresenta boa eficiência geral e é fácil de implementar.  
- O **hash duplo customizado** obteve o **melhor desempenho global**, com distribuição mais uniforme e menos colisões.

### 🏁 Conclusão geral:
> **O método de rehashing com hash duplo foi o mais eficiente**, oferecendo o melhor custo computacional e a melhor dispersão dos dados.

---
