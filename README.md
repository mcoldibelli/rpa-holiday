<div align=center>

# Vaga Ambiental/2S

Este repositório tem o objetivo de testar suas habilidades com relação a criação de uma API REST utilizando o framework **Spring Boot**, juntamente com o banco de dados **Postgres**

[Descrição do Problema](#page_facing_up-descrição-do-problema)  &nbsp;&bull;&nbsp; [Execução](#gear-executando-o-projeto) &nbsp;&bull;&nbsp; [Entrega](#package-entrega-do-projeto)

</div>

<br/>
<br/>

## :page_facing_up: Descrição do problema

Dada uma planilha de Excel contendo as colunas **estado** e **cidade**, monte um **RPA** em Java utilizando **Selenium**, de forma que ele leia os dados da planilha fornecida e busque dentro do site [feriados.com.br](https://www.feriados.com.br/) quais são os dias dos mêses em que essas cidades possuem feriados.

Com essas informações em mãos, crie uma tabela no **postgres** para salvar os dados recém coletados. Dados esses, que deverão ser enviados para nossa **API**, que realizará um teste, retornando uma mensagem de sucesso ou erro.


### :pushpin: Etapas

1. Faça o clone do projeto
2. Monte um RPA utilizando **Selenium**
3. Extraia os dados do site `feriados.com.br` com base na planilha fornecida
3. Salve os dados extraídos no **Postgres**
4. Busque os dados do **Postgres** e envie-os a nossa [API](#api)

### API

Como última etapa do nosso processo de avaliação envie os dados que estão salvos no banco para nossa API conforme as especificações abaixo:

- **MÉTODO:** `POST` 
- **URL:** `http://spprev.ambientalqvt.com.br/dinamico/avaliacao-vaga`

```txt
[BEARER TOKEN] <token_que_te_enviaremos>
```

```json
{
    "estado": "NOME_DO_ESTADO",
    "cidade": "NOME_DA_CIDADE",
    "feriados": [
        {
            "data": "DD/MM/YYYY",
            "tipo": "MUNICIPAL | NACIONAL",
            "feriado": "NOME_DO_FERIADO",
        },
        ...
    ]
}
```

#### :heavy_check_mark: Retorno de Sucesso

```json
// Não retorna dados
```

#### :x: Retorno de Erro

```json
{    
    "mensagem": "MENSAGEM_DE_ERRO"
}
```

<br/>

## :gear: Executando o projeto

```bash
docker-compose up --build
```

<br/>

## :package: Entrega do projeto

Monte um repositório público e compartilhe conosco o link do seu repositório